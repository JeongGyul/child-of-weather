// /js/activity.js

document.addEventListener("DOMContentLoaded", () => {
  const addBtn    = document.getElementById("btn-add-activity");
  const overlay   = document.getElementById("activity-modal-overlay");
  const form      = document.getElementById("activity-form");
  const closeBtn  = document.getElementById("btn-modal-close");
  const cancelBtn = document.getElementById("btn-modal-cancel");

  // 필수 요소가 하나라도 없으면 기능 수행 X
  if (!addBtn || !overlay || !form || !closeBtn || !cancelBtn) return;

  // 모달 열기
  const openModal = () => {
    overlay.classList.add("is-open"); // CSS에서 .modal-overlay.is-open 보이게 처리되어 있다고 가정
  };

  // 모달 닫기
  const closeModal = () => {
    overlay.classList.remove("is-open");
    form.reset();
  };

  // 버튼 이벤트
  addBtn.addEventListener("click", openModal);
  closeBtn.addEventListener("click", closeModal);
  cancelBtn.addEventListener("click", closeModal);

  // 오버레이(바깥 영역) 클릭 시 닫기
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) {
      closeModal();
    }
  });

  // 🔹 폼 제출: 더 이상 e.preventDefault() 쓰지 않음
  //    → 기본 동작대로 /activity.do 로 POST 전송
  form.addEventListener("submit", () => {
    // 여기서는 서버로 전송만 맡기고, 살짝 UX만 정리
    overlay.classList.remove("is-open");
    // form.reset();  // 새로고침/리다이렉트 되니까 안 해도 됨. 필요하면 주석 해제
    // ※ 절대 e.preventDefault() 쓰지 말 것!
  });
});
