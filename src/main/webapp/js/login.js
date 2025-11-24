/* login.js */

// 탭 전환 함수
function switchTab(tabName) {
    // 1. 모든 탭 내용 숨기기
    const contents = document.querySelectorAll('.tab-content');
    contents.forEach(function(el) {
        el.classList.remove('active');
    });

    // 2. 모든 탭 버튼 비활성화 스타일 적용 (회색 텍스트)
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(function(el) {
        el.classList.remove('active');
        el.classList.add('text-gray-500');
    });

    // 3. 선택된 탭 내용 보이기
    const selectedContent = document.getElementById('tab-' + tabName);
    if (selectedContent) {
        selectedContent.classList.add('active');
    }
    
    // 4. 선택된 버튼 활성화 스타일 적용 (흰색 배경, 검은 텍스트)
    const activeBtn = document.getElementById('btn-' + tabName);
    if (activeBtn) {
        activeBtn.classList.add('active');
        activeBtn.classList.remove('text-gray-500');
    }
};