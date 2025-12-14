package com.childofweather.service;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;

public class MemberService {
	
	private MemberDAO memberDAO = new MemberDAO();
	
	// 회원가입
	public boolean join(MemberDTO.JoinRequest dto) {
		return memberDAO.insert(dto);
	}
	
	// 로그인
	public MemberDTO.InfoResponse login(MemberDTO.LoginRequest dto) {
		boolean isLoginSuccess = memberDAO.login(dto);
		
		if(isLoginSuccess) {
			memberDAO.updateLastLogin(dto.getEmail());
			
			return memberDAO.getMember(dto);
		}
		return null;
	}
	
	// 마이페이지 정보 반환
	public MemberDTO.MyPageInfoResponse getMyPageInfo(MemberDTO.InfoResponse loginUser) {
		MemberDTO.MyPageInfoResponse myPageInfo = new MemberDTO.MyPageInfoResponse();
		int activityCount = memberDAO.getUserActivityCount(String.valueOf(loginUser.getMemberId()));
		
		myPageInfo.setMemberInfo(loginUser);
		myPageInfo.setActivityCount(activityCount);
		myPageInfo.setRouteCount(3);
		
		return myPageInfo;
	}
	
	// 마이페이지 정보 수정
	public boolean updateProfile(String originalEmail, String newName, String newEmail) {
		MemberDTO.MyPageEditRequest updateDTO = new MemberDTO.MyPageEditRequest();
		
		updateDTO.setName(newName);
		updateDTO.setEmail(newEmail);
		
		int result = memberDAO.updateProfile(originalEmail, updateDTO);
		return result > 0;
	}
}
