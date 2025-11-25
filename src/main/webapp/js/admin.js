// admin.js

// 1. 회원 삭제 확인
function confirmDelete(memberId, memberName) {
    // 리액트의 AlertDialog 대신 브라우저 기본 confirm 사용 (구현이 훨씬 간단함)
    const msg = `정말로 ${memberName}님을 강제 탈퇴시키시겠습니까?\n이 작업은 되돌릴 수 없습니다.`;
    
    if (confirm(msg)) {
        // 삭제 서블릿 호출
        location.href = 'deleteUser.do?id=' + memberId;
    }
}