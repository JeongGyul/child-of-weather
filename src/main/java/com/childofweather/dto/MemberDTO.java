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
		private int alertCount;
		
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
		public int getAlertCount() {
			return alertCount;
		}
		public void setAlertCount(int alertCount) {
			this.alertCount = alertCount;
		}
	}

    // --- member_activities 연관 데이터 (1:N) ---
    // 이 회원이 등록한 활동 목록
    private List<MemberActivity> activities = new ArrayList<>();

    // ====== activities getter / setter ======

    public List<MemberActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<MemberActivity> activities) {
        this.activities = activities;
    }

    public void addActivity(MemberActivity activity) {
        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        this.activities.add(activity);
    }

    // =====================================================
    //  내부 클래스: member_activities 테이블 매핑용 DTO
    //  (별도 파일 안 만들고 MemberDTO 안에 포함)
    // =====================================================

    public static class MemberActivity {

        // member_activities.member_activity_id (PK)
        private Long memberActivityId;

        // member_activities.member_id (FK → members.member_id)
        private Long memberId;

        // member_activities.activity_type_id (FK → activity_types.activity_type_id)
        private Long activityTypeId;

        // 날씨 조건
        private Integer minTemperature;
        private Integer maxTemperature;
        private Integer maxHumidity;
        private Integer maxPrecipitation;

        // 추천 타이밍
        private LocalDate recommendationDate;
        private LocalTime recommendationStartTime;
        private LocalTime recommendationEndTime;
        private String timingStatus; // BEST_NOW / PLANNED / ENDED

        private LocalDateTime lastCalculatedAt;

        // 생성/수정 일시
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Long getMemberActivityId() {
            return memberActivityId;
        }

        public void setMemberActivityId(Long memberActivityId) {
            this.memberActivityId = memberActivityId;
        }

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public Long getActivityTypeId() {
            return activityTypeId;
        }

        public void setActivityTypeId(Long activityTypeId) {
            this.activityTypeId = activityTypeId;
        }

        public Integer getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(Integer minTemperature) {
            this.minTemperature = minTemperature;
        }

        public Integer getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(Integer maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public Integer getMaxHumidity() {
            return maxHumidity;
        }

        public void setMaxHumidity(Integer maxHumidity) {
            this.maxHumidity = maxHumidity;
        }

        public Integer getMaxPrecipitation() {
            return maxPrecipitation;
        }

        public void setMaxPrecipitation(Integer maxPrecipitation) {
            this.maxPrecipitation = maxPrecipitation;
        }

        public LocalDate getRecommendationDate() {
            return recommendationDate;
        }

        public void setRecommendationDate(LocalDate recommendationDate) {
            this.recommendationDate = recommendationDate;
        }

        public LocalTime getRecommendationStartTime() {
            return recommendationStartTime;
        }

        public void setRecommendationStartTime(LocalTime recommendationStartTime) {
            this.recommendationStartTime = recommendationStartTime;
        }

        public LocalTime getRecommendationEndTime() {
            return recommendationEndTime;
        }

        public void setRecommendationEndTime(LocalTime recommendationEndTime) {
            this.recommendationEndTime = recommendationEndTime;
        }

        public String getTimingStatus() {
            return timingStatus;
        }

        public void setTimingStatus(String timingStatus) {
            this.timingStatus = timingStatus;
        }

        public LocalDateTime getLastCalculatedAt() {
            return lastCalculatedAt;
        }

        public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) {
            this.lastCalculatedAt = lastCalculatedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
