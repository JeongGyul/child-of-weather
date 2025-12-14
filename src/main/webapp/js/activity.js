// /js/activity.js

document.addEventListener("DOMContentLoaded", () => {
    console.log("Activity.js loaded");

    const addBtn    = document.getElementById("btn-add-activity");
    const overlay   = document.getElementById("activity-modal-overlay");
    const form      = document.getElementById("activity-form");
    const closeBtn  = document.getElementById("btn-modal-close");
    const cancelBtn = document.getElementById("btn-modal-cancel");

    let weatherCache = null;

    // 🕒 시간 계산 헬퍼 함수 (시작 시간 + 소요 시간 = 종료 시간)
    const calculateEndTime = (startTimeStr, durationMin) => {
        // "15:00" -> 시, 분 분리
        const [hour, minute] = startTimeStr.split(':').map(Number);
        
        const date = new Date();
        date.setHours(hour);
        date.setMinutes(minute + durationMin); // 소요 시간 더하기

        const endHour = String(date.getHours()).padStart(2, '0');
        const endMin = String(date.getMinutes()).padStart(2, '0');

        return `${endHour}:${endMin}`;
    };

    // 2. 날씨 데이터 가져오기
    const fetchWeatherData = () => {
        if (!navigator.geolocation) {
            updateAllCardsStatus('위치 미지원', 'bad');
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const { latitude, longitude } = position.coords;
                const contextPath = document.body.getAttribute('data-context-path') || '';
                const url = `${contextPath}/weather/short?lat=${latitude}&lon=${longitude}`;

                console.log("Fetching weather from:", url);

                fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' } })
                .then(res => {
                    if (!res.ok) throw new Error(`HTTP Error ${res.status}`);
                    return res.json();
                })
                .then(data => {
                    console.log("Weather Data Loaded:", data);
                    weatherCache = data;
                    updateActivityList(data);
                })
                .catch(err => {
                    console.error("Weather fetch failed:", err);
                    updateAllCardsStatus('날씨 오류', 'bad');
                });
            },
            (err) => {
                console.warn("Geolocation error:", err);
                updateAllCardsStatus('위치 확인 불가', 'bad');
            }
        );
    };

    // 3. 활동 리스트 업데이트
    const updateActivityList = (data) => {
        const hourly = data.hourly || [];
        const cards = document.querySelectorAll('.activity-card');

        if (hourly.length === 0) {
            updateAllCardsStatus('예보 데이터 없음', 'bad');
            return;
        }

        console.group("활동별 분석 결과");

        cards.forEach(card => {
            const activityId = card.dataset.id || 'Unknown';
            const duration = parseInt(card.dataset.duration) || 60; // 소요 시간 (기본 60분)
            
            // 조건 읽기
            const minTemp = parseFloat(card.dataset.minTemp);
            const maxTemp = parseFloat(card.dataset.maxTemp);
            const maxPop = parseFloat(card.dataset.maxPop);
            const maxHumid = parseFloat(card.dataset.maxHumid);

            const statusBadge = card.querySelector('.js-status-badge');
            const timingResult = card.querySelector('.js-timing-result');

            // 조건에 맞는 가장 빠른 시간 찾기
            const bestHour = hourly.find(h => {
                const temp = h.temperature;
                const pop = h.pop;
                const humid = h.humidity || 0; 

                if (!isNaN(minTemp) && temp < minTemp) return false;
                if (!isNaN(maxTemp) && temp > maxTemp) return false;
                if (!isNaN(maxPop) && pop > maxPop) return false;
                if (!isNaN(maxHumid) && humid > maxHumid) return false;

                return true; 
            });

            if (bestHour) {
                // [수정됨] 종료 시간 계산
                const endTime = calculateEndTime(bestHour.time, duration);
                
                // 성공 표시
                console.log(`[#${activityId}] 추천: ${bestHour.time} ~ ${endTime}`);
                
                if(statusBadge) {
                    statusBadge.textContent = '진행 가능';
                    statusBadge.className = 'status-badge status-good';
                    statusBadge.style.backgroundColor = '#e8f5e9';
                    statusBadge.style.color = '#2e7d32';
                }
                if(timingResult) {
                    // [수정됨] "15:00 ~ 16:00 추천" 형식으로 변경
                    timingResult.textContent = `${bestHour.time} ~ ${endTime} 추천`;
                    timingResult.style.color = '#2e7d32';
                    timingResult.style.fontWeight = 'bold';
                }
            } else {
                // 실패 표시
                console.warn(`[#${activityId}] 적합한 시간 없음`);
                
                if(statusBadge) {
                    statusBadge.textContent = '보류 권장';
                    statusBadge.className = 'status-badge status-bad';
                    statusBadge.style.backgroundColor = '#ffebee';
                    statusBadge.style.color = '#c62828';
                }
                if(timingResult) {
                    timingResult.textContent = '조건에 맞는 시간 없음';
                    timingResult.style.color = '#c62828';
                    timingResult.style.fontWeight = 'normal';
                }
            }
        });
        
        console.groupEnd();
    };

    // 오류/상태 일괄 처리
    const updateAllCardsStatus = (msg, type) => {
        document.querySelectorAll('.activity-card').forEach(card => {
            const statusBadge = card.querySelector('.js-status-badge');
            const timingResult = card.querySelector('.js-timing-result');
            
            if(statusBadge) statusBadge.textContent = '확인 불가';
            if(timingResult) {
                timingResult.textContent = msg;
                timingResult.style.color = type === 'bad' ? '#c62828' : '#666';
            }
        });
    };

    // 4. 모달 기능 (값 자동 채우기)
    const openModal = () => {
        overlay.classList.add("is-open");

        if (weatherCache && weatherCache.hourly && weatherCache.hourly.length > 0) {
            const hourly = weatherCache.hourly;
            
            const temps = hourly.map(h => h.temperature).filter(t => typeof t === 'number');
            const pops = hourly.map(h => h.pop).filter(p => typeof p === 'number');
            
            const minT = temps.length ? Math.min(...temps) : 10;
            const maxT = temps.length ? Math.max(...temps) : 30;
            const maxP = pops.length ? Math.max(...pops) : 20;
            const curHumid = weatherCache.humidity || 50;

            const minTempInput = document.getElementById('min-temp');
            const maxTempInput = document.getElementById('max-temp');
            const maxHumidInput = document.getElementById('max-humidity');
            const maxPopInput = document.getElementById('max-pop');

            if(minTempInput) minTempInput.value = Math.floor(minT); 
            if(maxTempInput) maxTempInput.value = Math.ceil(maxT);
            if(maxHumidInput) maxHumidInput.value = curHumid; 
            if(maxPopInput) maxPopInput.value = Math.max(20, maxP + 10); 
        }
    };

    const closeModal = () => {
        overlay.classList.remove("is-open");
        form.reset();
    };

    // 5. 이벤트 리스너
    if (addBtn) addBtn.addEventListener("click", openModal);
    if (closeBtn) closeBtn.addEventListener("click", closeModal);
    if (cancelBtn) cancelBtn.addEventListener("click", closeModal);
    
    if (overlay) {
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) closeModal();
        });
    }

    fetchWeatherData();
});