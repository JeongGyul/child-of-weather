package com.childofweather.service;

import java.util.List;

import com.childofweather.dao.ActivityDAO;
import com.childofweather.dto.ActivityDTO;

public class ActivityService {
	private ActivityDAO activityDAO = new ActivityDAO();
	
	// 특정 사용자의 활동 목록 반환
	public List<ActivityDTO.Response> getActivities(Long memberId) {
		return activityDAO.getActivities(memberId);
	}
	
	// 특정 사용자의 활동 삭제
	public void deleteMemberActivity(long memberActivityId, long memberId) {
		activityDAO.deleteMemberActivity(memberActivityId, memberId);
	}
	
	// 활동 추가
	public void insertMemberActivity(ActivityDTO.Request dto) {
		activityDAO.insertMemberActivity(dto);
	}
}
