// admin.js (수정할 부분)

// 1. 회원 삭제 확인
function confirmDelete(memberId, memberName) {
    const msg = `정말로 ${memberName}님을 강제 탈퇴시키시겠습니까?\n이 작업은 되돌릴 수 없습니다.`;
    
    if (confirm(msg)) {
        // [수정]: 상대 경로(admin/deleteUser.do)를 절대 경로로 변경
        // contextPath + 서블릿 매핑 경로(/admin/deleteUser.do)를 조합합니다.
        location.href = contextPath + '/admin/deleteUser.do?id=' + memberId;
    }
}