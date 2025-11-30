// dashboard.js
// 대시보드 페이지 로드 시, 브라우저 위치를 가져와 WeatherServlet(/weather/short)으로 요청하는 스크립트

(function () {
    const DEFAULT_CONTEXT_PATH = '';
    const contextPath = window.appContextPath || DEFAULT_CONTEXT_PATH;
    const WEATHER_API_URL = contextPath + '/weather/short';

    window.addEventListener('DOMContentLoaded', function () {
        // 화면 요소들 캐싱
        const locationEl = document.querySelector('.cw-left-location');
        const tempEl = document.querySelector('.cw-temp-value');
        const conditionEl = document.querySelector('.cw-condition');
        const subTextEl = document.querySelector('.cw-left-sub');

        const statRows = document.querySelectorAll('.cw-right .cw-stat-row');
        const humidityEl = statRows[0] ? statRows[0].querySelector('.cw-stat-pill:nth-child(1)') : null;
        const windEl = statRows[0] ? statRows[0].querySelector('.cw-stat-pill:nth-child(2)') : null;
        const rainEl = statRows[1] ? statRows[1].querySelector('.cw-stat-pill:nth-child(1)') : null;

        // 대시보드가 구조가 바뀌어서 요소를 못 찾으면 그냥 종료
        if (!locationEl || !tempEl || !conditionEl) {
            console.warn('dashboard.js: 필요한 요소를 찾지 못했습니다.');
            return;
        }

        // 초기 상태 표시
        locationEl.textContent = '현재 날씨 · 위치 정보를 확인중입니다';
        tempEl.textContent = '--';
        conditionEl.textContent = '위치 확인 중';
        if (subTextEl) subTextEl.textContent = '현재 위치를 불러오는 중입니다.';

        if (humidityEl) humidityEl.textContent = '습도 -';
        if (windEl) windEl.textContent = '바람 -';
        if (rainEl) rainEl.textContent = '강수 -';

        // 브라우저 위치 기능 체크
        if (!navigator.geolocation) {
            conditionEl.textContent = '위치 기능 미지원';
            return;
        }

        // 위치 요청
        navigator.geolocation.getCurrentPosition(onGeoSuccess, onGeoError, {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        });

        function onGeoSuccess(position) {
            console.log('Geolocation success raw position:', position);

            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            console.log('Geolocation coords -> lat:', lat, 'lon:', lon);

            requestWeather(lat, lon)
                .then(function (data) {
                    console.log('Weather data from server:', data);
                    updateCurrentWeatherView({
                        locationEl,
                        tempEl,
                        conditionEl,
                        subTextEl,     // ★ 추가
                        humidityEl,
                        windEl,
                        rainEl,
                        hourlyContainer: document.getElementById('hourly-row') // 3번에서 쓸 예정
                    }, data);
                })
                .catch(function (err) {
                    console.error('Weather request error:', err);
                    conditionEl.textContent = '날씨 정보를 불러오지 못했습니다';
                });
        }

        function onGeoError(error) {
            console.warn('Geolocation error:', error);
            let msg = '위치 정보를 가져올 수 없습니다';

            switch (error.code) {
                case error.PERMISSION_DENIED:
                    msg = '위치 권한이 거부되었습니다. 브라우저 설정을 확인해주세요';
                    break;
                case error.POSITION_UNAVAILABLE:
                    msg = '위치 정보를 사용할 수 없습니다';
                    break;
                case error.TIMEOUT:
                    msg = '위치 정보 요청 시간이 초과되었습니다';
                    break;
            }

            conditionEl.textContent = msg;
        }
    });

    // 서버에 날씨 정보 요청
    function requestWeather(lat, lon) {
        const params = new URLSearchParams({
            lat: String(lat),
            lon: String(lon)
        });

        const url = WEATHER_API_URL + '?' + params.toString();

        return fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        }).then(function (res) {
            if (!res.ok) {
                throw new Error('HTTP error ' + res.status);
            }
            return res.json();
        });
    }

    /**
     * 현재 대시보드 카드에 데이터 반영
     * 이 함수는 WeatherServlet이 아래 형태의 JSON을 준다고 가정하고 있음:
     *
     * {
     *   "locationName": "경북 포항시 남구",
     *   "temperature": 18,          // 숫자
     *   "conditionText": "맑음",
     *   "humidity": 45,             // %
     *   "windSpeed": 2.5,           // m/s
     *   "precipitationProb": 10     // %
     * }
     *
     * 나중에 Servlet에서 실제로 어떻게 내려줄지 확정되면 여기만 맞춰 수정하면 됨.
     */
    function updateCurrentWeatherView(els, data) {
        const {
            locationEl,
            tempEl,
            conditionEl,
            subTextEl,
            humidityEl,
            windEl,
            rainEl,
            hourlyContainer
        } = els;

        if (locationEl && data.locationName) {
            locationEl.textContent = '현재 날씨 · ' + data.locationName;
        }

        if (tempEl && typeof data.temperature === 'number') {
            tempEl.textContent = data.temperature + '°';
        }

        if (conditionEl && data.conditionText) {
            conditionEl.textContent = data.conditionText;
        }

        // ★ 1번 기능: 상태 메시지
        if (subTextEl) {
            if (data.statusMessage) {
                subTextEl.textContent = data.statusMessage;
            } else {
                // 서버가 안 내려줬을 때 간단한 기본값
                subTextEl.textContent = '현재 기상 상태를 확인했습니다.';
            }
        }

        if (humidityEl && typeof data.humidity === 'number') {
            humidityEl.textContent = '습도 ' + data.humidity + '%';
        }

        if (windEl && typeof data.windSpeed === 'number') {
            windEl.textContent = '바람 ' + data.windSpeed + 'm/s';
        }

        if (rainEl && typeof data.precipitationProb === 'number') {
            rainEl.textContent = '강수 ' + data.precipitationProb + '%';
        }

        // ★ 3번 기능: 시간별 예보는 아래에서 처리 (hourlyContainer + data.hourly)
        if (hourlyContainer && Array.isArray(data.hourly)) {
            updateHourlyForecast(hourlyContainer, data.hourly);
        }
    }

    function updateHourlyForecast(container, hourlyList) {
        container.innerHTML = '';

        // 최대 5개까지만 표시 (원하면 숫자 조정)
        const maxItems = 5;
        const items = hourlyList.slice(0, maxItems);

        items.forEach(function (h) {
            const hourItem = document.createElement('div');
            hourItem.className = 'hour-item';

            const timeEl = document.createElement('div');
            timeEl.className = 'hour-time';
            timeEl.textContent = h.time; // "09:00" 형태

            const iconEl = document.createElement('div');
            iconEl.className = 'hour-icon';
            iconEl.textContent = getWeatherIcon(h.pty, h.pop); // 아래 함수 참고

            const tempEl = document.createElement('div');
            tempEl.className = 'hour-temp';
            if (typeof h.temperature === 'number') {
                tempEl.textContent = h.temperature + '°';
            } else {
                tempEl.textContent = '--';
            }

            const popEl = document.createElement('div');
            popEl.className = 'hour-pop';
            if (typeof h.pop === 'number' && h.pop >= 0) {
                popEl.textContent = h.pop + '%';
            } else {
                popEl.textContent = '-%';
            }

            hourItem.appendChild(timeEl);
            hourItem.appendChild(iconEl);
            hourItem.appendChild(tempEl);
            hourItem.appendChild(popEl);

            container.appendChild(hourItem);
        });
    }

// PTY(강수형태) + 강수확률로 아이콘 결정
    function getWeatherIcon(pty, pop) {
        if (pty === 0) {
            // 비/눈 없음
            if (typeof pop === 'number' && pop >= 60) {
                return '☁'; // 흐리고 비 가능성
            }
            return '☀'; // 기본 맑음
        }
        switch (pty) {
            case 1: return '🌧'; // 비
            case 2: return '🌨'; // 비/눈
            case 3: return '❄'; // 눈
            case 4: return '🌦'; // 소나기
            default: return '☁';
        }
    }


})();
