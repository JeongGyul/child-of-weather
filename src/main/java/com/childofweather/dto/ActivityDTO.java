package com.childofweather.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityDTO {
	
	public static class Request {
		private Long memberId;
		private Long activityTypeId;
		private Integer minTemperature;
        private Integer maxTemperature;
        private Integer maxHumidity;
        private Integer maxPrecipitation;
        
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
	}
	
	public static class Response {
		private Long memberActivityId;
		private Long activityTypeId;
		private String timingStatus; // BEST_NOW / PLANNED / ENDED
		private Integer minTemperature;
        private Integer maxTemperature;
        private Integer maxHumidity;
        private Integer maxPrecipitation;
        private LocalDate recommendationDate;
        private LocalTime recommendationStartTime;
        private LocalTime recommendationEndTime;
        
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
		public Long getMemberActivityId() {
			return memberActivityId;
		}
		public void setMemberActivityId(Long memberActivityId) {
			this.memberActivityId = memberActivityId;
		}
		public String getTimingStatus() {
			return timingStatus;
		}
		public void setTimingStatus(String timingStatus) {
			this.timingStatus = timingStatus;
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
	}

	public static class RecommendActivityResponse {
		private Long activityTypeId;
		private String activityName;
		private Integer defaultDurationMin;
		private String iconCode;

		public Long getActivityTypeId() { return activityTypeId; }
		public void setActivityTypeId(Long activityTypeId) { this.activityTypeId = activityTypeId; }
		public String getActivityName() { return activityName; }
		public void setActivityName(String activityName) { this.activityName = activityName; }
		public Integer getDefaultDurationTime() { return defaultDurationMin; }
		public void setDefaultDurationMin(Integer defaultDurationMin) { this.defaultDurationMin = defaultDurationMin; }
		public String getIconCode() { return iconCode; }
		public void setIconCode(String iconCode) { this.iconCode = iconCode; }
	}
}
