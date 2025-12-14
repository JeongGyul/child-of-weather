package com.childofweather.dto;

import java.sql.Date;
import java.sql.Time;
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
		private Integer minTemperature;
        private Integer maxTemperature;
        private Integer maxHumidity;
        private Integer maxPrecipitation;
		Recommendation recommendation;
		private Integer defaultDurationMin;
        
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

		public void setRecommendation(Recommendation recommendation) {
			this.recommendation = recommendation;
		}
		public Integer getDefaultDurationMin() { return defaultDurationMin; }
		public void setDurationMin(int defaultDurationMin) {
			this.defaultDurationMin = defaultDurationMin;
		}
	}

	public static class Recommendation {
		private LocalDate date;
		private LocalTime startTime;
		private LocalTime endTime;
		private String timingStatus;

		public Recommendation(LocalDate date, LocalTime startTime, LocalTime endTime, String timingStatus) {
			this.date = date;
			this.startTime = startTime;
			this.endTime = endTime;
			this.timingStatus = timingStatus;
		}

		public LocalDate getDate() { return date; }
		public LocalTime getStartTime() { return startTime; }
		public LocalTime getEndTime() { return endTime; }
		public String getTimingStatus() { return timingStatus; }
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
