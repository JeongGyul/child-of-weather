// /js/route.js

//------------------------------------------------------
// 전역 변수 (지도, 마커, 라인)
//------------------------------------------------------
let map = null;
let startMarker = null;
let endMarker = null;
let routePolyline = null;

//------------------------------------------------------
// 네이버 지도 초기화 (callback=initRouteMap)
//------------------------------------------------------
window.initRouteMap = function () {
  const mapEl = document.getElementById('route-map');
  if (!mapEl) return;

  map = new naver.maps.Map('route-map', {
    center: new naver.maps.LatLng(37.5665, 126.9780), // 서울 시청 기준
    zoom: 11,
  });
};

//------------------------------------------------------
// 주소 → 좌표 (Geocoder)
//------------------------------------------------------
function geocode(label, query) {
  return new Promise(function (resolve, reject) {
    if (!query) {
      reject(new Error(label + '를 입력해주세요.'));
      return;
    }

    naver.maps.Service.geocode({ query }, function (status, response) {
      if (status !== naver.maps.Service.Status.OK) {
        console.error('[Geocode 실패]', label, status, response);
        reject(new Error(label + ' 주소 검색 중 오류가 발생했습니다.'));
        return;
      }

      const addresses = response?.v2?.addresses || [];
      if (addresses.length === 0) {
        console.warn('[Geocode 검색 결과 없음]', label, query, response);
        reject(
          new Error(
            label +
              '에 대한 주소 검색 결과가 없습니다. 도로명/지번 주소를 입력해 주세요.'
          )
        );
        return;
      }

      const result = addresses[0];
      resolve({
        lat: parseFloat(result.y),
        lng: parseFloat(result.x),
        raw: result,
      });
    });
  });
}

//------------------------------------------------------
// 네이버 Local 검색 API (키워드 → 주소)
//  - "서울역", "강남역" 등은 Local 검색
//  - 이미 "서울특별시 용산구 ..." 같은 주소면 Local 생략
//------------------------------------------------------
async function searchPlace(label, keyword) {
  const contextPath = window.appContextPath || '';
  const trimmed = keyword.trim();

  // 이미 "주소"처럼 보이면 Local 검색 생략하고 그대로 주소 사용
  const looksLikeAddress = /[시군구]/.test(trimmed) && /\d/.test(trimmed);
  if (looksLikeAddress) {
    console.log('[Local 생략] 주소로 판단, 그대로 사용:', label, trimmed);
    return { title: trimmed, address: trimmed };
  }

  const params = new URLSearchParams({ query: trimmed });

  const res = await fetch(
    contextPath + '/naver/placeSearch?' + params.toString(),
    {
      method: 'GET',
      headers: { Accept: 'application/json' },
    }
  );

  if (!res.ok) {
    console.error('[Local 검색 오류]', label, res.status);
    throw new Error(label + ' 장소 검색 중 오류가 발생했습니다.');
  }

  const data = await res.json();

  if (!data.items || data.items.length === 0) {
    console.warn('[Local 검색 결과 없음]', label, trimmed, data);

    // total > 0 인데 items가 비어 있는 이상한 케이스 → 키워드를 주소로 fallback
    if (typeof data.total === 'number' && data.total > 0) {
      console.log(
        '[Local fallback] total>0 이지만 items 없음 → 키워드를 주소로 사용:',
        trimmed
      );
      return { title: trimmed, address: trimmed };
    }

    throw new Error(
      label +
        '에 대한 장소 검색 결과가 없습니다. 더 구체적인 이름을 입력해 주세요.'
    );
  }

  // 첫 번째 결과 사용
  const item = data.items[0];

  // <b>서울역</b> 같은 태그 제거
  const title = (item.title || '').replace(/<[^>]+>/g, '');
  const address = item.roadAddress || item.address || trimmed;

  return { title, address };
}

//------------------------------------------------------
// Directions API 호출 (백엔드 프록시 /naver/route)
//------------------------------------------------------
async function requestRouteFromServer(start, end) {
  const startParam = `${start.lng},${start.lat}`; // "경도,위도"
  const endParam = `${end.lng},${end.lat}`;

  const params = new URLSearchParams({ start: startParam, goal: endParam });

  const contextPath = window.appContextPath || '';
  const url = contextPath + '/naver/route?' + params.toString();

  const res = await fetch(url, {
    method: 'GET',
    headers: { Accept: 'application/json' },
  });

  if (!res.ok) {
    console.error('[Directions HTTP 오류]', res.status);
    throw new Error('네이버 길찾기 API 호출 실패: ' + res.status);
  }

  const data = await res.json();
  console.log('[Directions 응답]', data);

  // 에러 필드가 있으면
  if (data.error) {
    console.error('[Directions 에러]', data.error);
    throw new Error(
      '길찾기 API 에러: ' +
        (data.error.message || JSON.stringify(data.error))
    );
  }

  // 경로 후보 선택 (traoptimal / trafast)
  const routeData =
    (data.route && data.route.traoptimal && data.route.traoptimal[0]) ||
    (data.route && data.route.trafast && data.route.trafast[0]) ||
    null;

  if (!routeData || !Array.isArray(routeData.path)) {
    console.error('[Directions 경로 없음]', data);
    throw new Error('경로 데이터가 없습니다. (길찾기 응답에 path 없음)');
  }

  return routeData.path.map(
    (p) => new naver.maps.LatLng(p[1], p[0]) // [lng,lat] → (lat,lng)
  );
}

//------------------------------------------------------
// 지도에 마커 + 경로 그리기
//------------------------------------------------------
function drawMarkersAndRouteLine(pathCoords, start, end) {
  if (!map) return;

  if (startMarker) startMarker.setMap(null);
  startMarker = new naver.maps.Marker({
    position: new naver.maps.LatLng(start.lat, start.lng),
    map,
  });

  if (endMarker) endMarker.setMap(null);
  endMarker = new naver.maps.Marker({
    position: new naver.maps.LatLng(end.lat, end.lng),
    map,
  });

  if (routePolyline) routePolyline.setMap(null);
  routePolyline = new naver.maps.Polyline({
    map,
    path: pathCoords,
    strokeColor: '#2563eb',
    strokeOpacity: 0.9,
    strokeWeight: 4,
  });

  const bounds = new naver.maps.LatLngBounds(
    pathCoords[0],
    pathCoords[pathCoords.length - 1]
  );
  map.fitBounds(bounds);
}

//------------------------------------------------------
// 페이지 초기화 & 이벤트
//------------------------------------------------------
(function () {
  let routes = [];
  const DEFAULT_WEEKDAYS = ['월', '화', '수', '목', '금'];

  document.addEventListener('DOMContentLoaded', function () {
    const routeListEl = document.getElementById('route-list');
    const routeEmptyEl = document.getElementById('route-empty');

    const openBtn = document.getElementById('btn-open-route-modal');
    const overlay = document.getElementById('route-modal-overlay');
    const closeBtn = document.getElementById('btn-close-route-modal');
    const cancelBtn = document.getElementById('btn-cancel-route');
    const form = document.getElementById('route-form');

    const startInput = document.getElementById('search-start');
    const endInput = document.getElementById('search-end');
    const searchBtn = document.getElementById('btn-search-route');

    //--------------------------------------------------
    // 모달 열기/닫기
    //--------------------------------------------------
    function openModal() {
      overlay.classList.add('is-open');
    }
    function closeModal() {
      overlay.classList.remove('is-open');
      form.reset();
    }

    openBtn?.addEventListener('click', openModal);
    closeBtn?.addEventListener('click', closeModal);
    cancelBtn?.addEventListener('click', closeModal);

    overlay?.addEventListener('click', (e) => {
      if (e.target === overlay) closeModal();
    });

    //--------------------------------------------------
    // 경로 카드 렌더링
    //--------------------------------------------------
    function renderRoutes() {
      routeListEl.innerHTML = '';

      if (routes.length === 0) {
        routeEmptyEl.style.display = 'block';
        return;
      }

      routeEmptyEl.style.display = 'none';

      routes.forEach((route) => {
        const card = document.createElement('div');
        card.className = 'route-card';

        const header = document.createElement('div');
        header.className = 'route-card-header';

        const left = document.createElement('div');
        left.className = 'route-card-header-left';

        const icon = document.createElement('div');
        icon.className = 'route-icon';
        icon.textContent = route.icon || '🚗';

        const titleWrap = document.createElement('div');
        const main = document.createElement('div');
        main.className = 'route-title-main';
        main.textContent = route.name;

        const sub = document.createElement('div');
        sub.className = 'route-title-sub';
        sub.innerHTML = `📍 ${route.start} → ${route.end}`;

        titleWrap.appendChild(main);
        titleWrap.appendChild(sub);
        left.appendChild(icon);
        left.appendChild(titleWrap);

        const delBtn = document.createElement('button');
        delBtn.type = 'button';
        delBtn.className = 'route-delete-btn';
        delBtn.textContent = '삭제';
        delBtn.dataset.id = route.id;

        header.appendChild(left);
        header.appendChild(delBtn);
        card.appendChild(header);

        // 일정 섹션
        const scheduleTitle = document.createElement('div');
        scheduleTitle.className = 'route-section-title';
        scheduleTitle.textContent = '예정 일정';

        const scheduleList = document.createElement('div');
        scheduleList.className = 'schedule-list';

        route.schedules.forEach((s) => {
          const row = document.createElement('div');
          row.className = 'schedule-row';

          const time = document.createElement('span');
          time.className = 'schedule-time';
          time.textContent =
            s.time + (s.arrivalTime ? ' ~ ' + s.arrivalTime : '');

          const badge = document.createElement('span');
          badge.className =
            'schedule-type-badge' + (s.type === 'return' ? ' return' : '');
          badge.textContent = s.type === 'return' ? '귀가' : '출발';

          const daysEl = document.createElement('div');
          daysEl.className = 'schedule-days';

          (s.days || []).forEach((d) => {
            const chip = document.createElement('span');
            chip.className = 'schedule-day-chip';
            chip.textContent = d;
            daysEl.appendChild(chip);
          });

          row.appendChild(time);
          row.appendChild(badge);
          row.appendChild(daysEl);
          scheduleList.appendChild(row);
        });

        card.appendChild(scheduleTitle);
        card.appendChild(scheduleList);

        // 알림 섹션
        const alertTitle = document.createElement('div');
        alertTitle.className = 'route-section-title';
        alertTitle.textContent = '알림';
        card.appendChild(alertTitle);

        if (route.alerts && route.alerts.length > 0) {
          const alertList = document.createElement('div');
          alertList.className = 'alert-list';

          route.alerts.forEach((a) => {
            const alertBox = document.createElement('div');
            alertBox.className =
              'alert-box ' + (a.type === 'caution' ? 'caution' : 'good');
            alertBox.innerHTML =
              (a.type === 'caution' ? '⚠️ ' : '✅ ') + a.message;
            alertList.appendChild(alertBox);
          });

          card.appendChild(alertList);
        } else {
          const emptyAlert = document.createElement('div');
          emptyAlert.className = 'alert-empty';
          emptyAlert.textContent = '현재 특별한 알림이 없습니다.';
          card.appendChild(emptyAlert);
        }

        routeListEl.appendChild(card);
      });

      // 삭제 버튼
      routeListEl.querySelectorAll('.route-delete-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
          const id = Number(btn.dataset.id);
          routes = routes.filter((r) => r.id !== id);
          renderRoutes();
        });
      });
    }

    renderRoutes();

    //--------------------------------------------------
    // 경로 추가 폼 제출
    //--------------------------------------------------
    form.addEventListener('submit', function (e) {
      e.preventDefault();

      const fd = new FormData(form);

      const name = String(fd.get('name') || '').trim();
      const icon = String(fd.get('icon') || '');
      const start = String(fd.get('start') || '').trim();
      const end = String(fd.get('end') || '').trim();
      const time = String(fd.get('time') || '');
      const arrivalTimeRaw = String(fd.get('arrivalTime') || '');
      const arrivalTime = arrivalTimeRaw || undefined;
      const daysSelected = fd.getAll('days').map((d) => String(d));
      const days = daysSelected.length > 0 ? daysSelected : DEFAULT_WEEKDAYS;

      if (!name || !start || !end || !time) {
        alert('필수 항목을 입력해주세요.');
        return;
      }

      const newRoute = {
        id: Date.now(),
        name,
        icon: icon || '🚗',
        start,
        end,
        schedules: [
          {
            time,
            arrivalTime,
            type: 'departure',
            days,
          },
        ],
        alerts: [],
      };

      routes.push(newRoute);
      renderRoutes();
      overlay.classList.remove('is-open');
      form.reset();
    });

    //--------------------------------------------------
    // 경로 검색 버튼 (장소명 → 주소 → 좌표 → 길찾기)
//--------------------------------------------------
    if (startInput && endInput && searchBtn) {
      searchBtn.addEventListener('click', async function () {
        if (!map) {
          alert('지도가 아직 준비되지 않았습니다.');
          return;
        }

        const startText = startInput.value.trim();
        const endText = endInput.value.trim();

        if (!startText || !endText) {
          alert('출발지와 도착지를 모두 입력해주세요.');
          return;
        }

        try {
          // 1) 장소명/주소 → 주소
          const startPlace = await searchPlace('출발지', startText);
          const endPlace = await searchPlace('도착지', endText);

          startInput.value = startPlace.address;
          endInput.value = endPlace.address;

          // 2) 주소 → 좌표
          const start = await geocode('출발지', startPlace.address);
          const end = await geocode('도착지', endPlace.address);

          // 3) 길찾기 API
          const pathCoords = await requestRouteFromServer(start, end);

          // 4) 지도에 표시
          drawMarkersAndRouteLine(pathCoords, start, end);
        } catch (err) {
          console.error(err);
          alert(err.message || '경로를 찾는 중 오류가 발생했습니다.');
        }
      });
    }
  });
})();
