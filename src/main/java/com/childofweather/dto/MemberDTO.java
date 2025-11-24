package com.childofweather.dto;

public class MemberDTO {

    // --- DB 컬럼 매핑 ---
    // members.member_id (PK)
    private Long memberId;

    // members.name
    private String name;

    // members.email (로그인 ID, UNIQUE)
    private String email;

    // members.password
    private String password;

    // members.role (USER / ADMIN 등)
    private String role;

    // --- 기본 생성자 ---
    public MemberDTO() {
    }

    // --- getter / setter ---

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}