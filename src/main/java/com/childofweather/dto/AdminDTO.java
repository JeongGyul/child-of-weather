package com.childofweather.dto;

import java.util.List;

public class AdminDTO {
	public static class AdminPageResponse {
		private List<MemberDTO.InfoResponse> userList;
        private int totalUsers;
        private int adminCount;
        private int userCount;
        private int newJoinCount;
        private int activeUserCount;
        
		public List<MemberDTO.InfoResponse> getUserList() {
			return userList;
		}
		public void setUserList(List<MemberDTO.InfoResponse> userList) {
			this.userList = userList;
		}
		public int getTotalUsers() {
			return totalUsers;
		}
		public void setTotalUsers(int totalUsers) {
			this.totalUsers = totalUsers;
		}
		public int getAdminCount() {
			return adminCount;
		}
		public void setAdminCount(int adminCount) {
			this.adminCount = adminCount;
		}
		public int getUserCount() {
			return userCount;
		}
		public void setUserCount(int userCount) {
			this.userCount = userCount;
		}
		public int getNewJoinCount() {
			return newJoinCount;
		}
		public void setNewJoinCount(int newJoinCount) {
			this.newJoinCount = newJoinCount;
		}
		public int getActiveUserCount() {
			return activeUserCount;
		}
		public void setActiveUserCount(int activeUserCount) {
			this.activeUserCount = activeUserCount;
		}
	}
}
