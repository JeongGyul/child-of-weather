package com.childofweather.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MemberDTO {
	
	// 회원가입용 (JoinServlet에서 사용)
	public static class JoinRequest {
		private String email;
		private String password;
		private String name;
		
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
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	// 로그인용 (LoginServlet에서 사용)
	public static class LoginRequest {
		private String email;
		private String password;
		
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
	}
	
	// 마이페이지에서 이름, 이메일 수정용 (MyPageEditServlet에서 사용)
	public static class MyPageEditRequest {
		private String name;
		private String email;
		
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
	}
	
	// 세션 저장 및 마이페이지 출력용 (LoginServlet, MyPageServlet에서 사용)
	public static class InfoResponse {
		private Long memberId;
		private String email;
		private String name;
		private String role;
		private LocalDate createdAt;
		private LocalDate lastLoginAt;
		
		public Long getMemberId() {
			return memberId;
		}
		public void setMemberId(Long memberId) {
			this.memberId = memberId;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getRole() {
			return role;
		}
		// 화면 출력용 한글 변환 메서드
		public String getRoleLabel() {
			if("USER".equals(this.role)) {
				return "일반 유저";
			}
			return "관리자";
		}
		public void setRole(String role) {
			this.role = role;
		}
		public LocalDate getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDate createdAt) {
			this.createdAt = createdAt;
		}
		public LocalDate getLastLoginAt() {
			return lastLoginAt;
		}
		public void setLastLoginAt(LocalDate lastLoginAt) {
			this.lastLoginAt = lastLoginAt;
		}
	}
	
	// 마이페이지 출력용 (MyPageServlet에서 사용)
	public static class MyPageInfoResponse {
		private MemberDTO.InfoResponse memberInfo;
		private int activityCount;
		private int routeCount;
		
		public MemberDTO.InfoResponse getMemberInfo() {
			return memberInfo;
		}
		public void setMemberInfo(MemberDTO.InfoResponse memberInfo) {
			this.memberInfo = memberInfo;
		}
		public int getActivityCount() {
			return activityCount;
		}
		public void setActivityCount(int activityCount) {
			this.activityCount = activityCount;
		}
		public int getRouteCount() {
			return routeCount;
		}
		public void setRouteCount(int routeCount) {
			this.routeCount = routeCount;
		}
	}
}
