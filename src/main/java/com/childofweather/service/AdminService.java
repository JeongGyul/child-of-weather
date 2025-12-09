package com.childofweather.service;

import java.util.List;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.AdminDTO;
import com.childofweather.dto.MemberDTO;

public class AdminService {
	private MemberDAO memberDAO = new MemberDAO();
	
	// 관리자 페이지에 필요한 정보 반환
	public AdminDTO.AdminPageResponse getAdminPageInfo() {
		AdminDTO.AdminPageResponse dto = new AdminDTO.AdminPageResponse();
		
        List<MemberDTO.InfoResponse> userList = memberDAO.getAllMembers();
        
        dto.setUserList(userList);
        dto.setTotalUsers(userList.size());
        dto.setAdminCount(memberDAO.getAdminCount());
        dto.setUserCount(memberDAO.getUserCount());
        dto.setNewJoinCount(memberDAO.getNewUserCount());
        dto.setActiveUserCount(memberDAO.getActiveUserCount());
        
        return dto;
	}
}
