package com.childofweather.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
		private Recommendation recommendation; // private 접근 제어자 명시 권장
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

		// ▼ [중요] 이 Getter 메서드가 없어서 에러가 났습니다. 추가됨!
		public Recommendation getRecommendation() {
			return recommendation;
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

		// 화면 표시용 날짜 (예: "05.20(월)")
		public String getFormattedDate() {
			if (date == null) return "";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd(E)", Locale.KOREAN);
			return date.format(formatter);
		}

		// 화면 표시용 시간 (예: "15:00")
		public String getFormattedStartTime() {
			return (startTime != null) ? startTime.toString() : "";
		}

		public String getFormattedEndTime() {
			return (endTime != null) ? endTime.toString() : "";
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