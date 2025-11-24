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

// 2. 역할 변경 (Select Box 변경 시 즉시 반영)
function handleRoleChange(memberId, selectElement) {
    const newRole = selectElement.value; // 'USER' or 'ADMIN'
    
    if(confirm('이 회원의 권한을 변경하시겠습니까?')) {
        // 권한 변경 서블릿 호출
        location.href = 'updateRole.do?id=' + memberId + '&role=' + newRole;
    } else {
        // 취소 시 원래대로 되돌리기 (새로고침)
        location.reload();
    }
}

// 3. 검색 (엔터키)
function handleSearch(event) {
    if (event.key === 'Enter') {
        const keyword = document.getElementById('userSearch').value;
        location.href = 'userList.do?keyword=' + encodeURIComponent(keyword);
    }
}