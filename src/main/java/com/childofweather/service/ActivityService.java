package com.childofweather.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.childofweather.dao.ActivityDAO;
import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.WeatherDTO;

public class ActivityService {
	private ActivityDAO activityDAO = new ActivityDAO();
	
	// 특정 사용자의 활동 목록 반환
	public List<ActivityDTO.Response> getActivities(Long memberId, List<WeatherDTO.HourlyForecast> hourly) {
		List<ActivityDTO.Response> activities = activityDAO.getActivities(memberId);

		if(activities == null || activities.isEmpty() || hourly == null || hourly.isEmpty()) {
			return activities;
		}

		List<WeatherDTO.HourlyForecast> sorted = new ArrayList<>(hourly);
		sorted.sort(Comparator.comparing(h -> toLocalDateTime(h.getBaseDateTime())));

		ZoneId kst = ZoneId.of("Asia/Seoul");
		LocalDateTime now = LocalDateTime.now(kst);
		LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);

		// 활동별 추천 계산 후 DB 업데이트
		for(ActivityDTO.Response activity : activities) {
			Long memberActivityId = activity.getMemberActivityId();
			Long activityTypeId = activity.getActivityTypeId();

			Integer durationMin = (activity.getDefaultDurationMin() == null || activity.getDefaultDurationMin() <= 0)
					? 60
					: activity.getDefaultDurationMin();

			ActivityDTO.Recommendation recommendation = calculateRecommendation(activityTypeId, durationMin, sorted, nowHour, now);

			if(recommendation == null) {
				// 추천 없음: 추천 값 NULL, 상태 ENDED
				activityDAO.updateRecommendation(
						memberActivityId,
						memberId,
						null,
						null,
						null,
						"ENDED");
			} else {
				activityDAO.updateRecommendation(
						memberActivityId,
						memberId,
						recommendation.getDate(),
						recommendation.getStartTime(),
						recommendation.getEndTime(),
						recommendation.getTimingStatus()
				);
			}
		}


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

	private ActivityDTO.Recommendation calculateRecommendation(
			Long activityTypeId,
			Integer durationMin,
			List<WeatherDTO.HourlyForecast> hourlySorted,
			LocalDateTime nowHour,
			LocalDateTime now
			){

		long stepMinutes = 60;
		if (hourlySorted.size() >= 2) {
			LocalDateTime t0 = toLocalDateTime(hourlySorted.get(0).getBaseDateTime());
			LocalDateTime t1 = toLocalDateTime(hourlySorted.get(1).getBaseDateTime());
			if (t0 != null && t1 != null) {
				long diff = java.time.Duration.between(t0, t1).toMinutes();
				if (diff > 0) stepMinutes = diff;
			}
		}

		int needSlot = (int) Math.ceil(durationMin / (double) stepMinutes);

		for(int i = 0; i <= hourlySorted.size() - needSlot; i++) {
			LocalDateTime start = toLocalDateTime(hourlySorted.get(i).getBaseDateTime());

			//과거 제외한 현재 시간대부터 체크
			if(start.isBefore(nowHour)) continue;

			boolean ok = true;

			// 연속 시간대 + 룰 체크
			for(int j = 0; j < needSlot; j++) {
				WeatherDTO.HourlyForecast h = hourlySorted.get(i + j);
				LocalDateTime t = toLocalDateTime(h.getBaseDateTime());

				if(t == null || !t.equals(start.plusMinutes(stepMinutes * j)) || !passRule(activityTypeId, h)) {
					ok = false;
					break;
				};
			}

			if(!ok) continue;

			LocalDateTime endDt = start.plusMinutes(durationMin);
			if(!endDt.toLocalDate().equals(start.toLocalDate())) continue;

			String status = now.isBefore(start) ? "PLANNED" : (now.isBefore(endDt) ? "BEST_NOW" : "ENDED");

			return new ActivityDTO.Recommendation(
					start.toLocalDate(),
					start.toLocalTime(),
					endDt.toLocalTime(),
					status
			);
		}

		return null;
	}

	private boolean passRule(Long activityTypeId, WeatherDTO.HourlyForecast h) {
		// 공통: 비/눈 등 강수형태가 있으면 제외
		Integer pty = h.getPty();

		if(pty == null || pty != 0) return false;
		int pop = (h.getPop() == null) ? 100 : h.getPop();				// 강수 확률
		int reh = (h.getHumidity() == null) ? 100 : h.getHumidity();	// 습도
		double tmp = (h.getTemperature1h() == null) ? Double.NaN : h.getTemperature1h();

		switch (activityTypeId.intValue()) {
			case 1:		// 빨래
				return reh <= 25 && pop <= 30;  // 습도 25 이하, 강수 확률 30 이하
			case 2:		// 세차
				return pop <= 10; // 강수 확률 10 이하
			default:	// 무난한 야외 활동
				if (!Double.isNaN(tmp) && (tmp < -5.0 || tmp > 35.0)) return false;
				return pop <= 30;
		}
	}

	private LocalDateTime toLocalDateTime(WeatherDTO.BaseDateTime dt) {
		if (dt == null) return null;
		try {
			return LocalDateTime.parse(
					dt.getBaseDate() + dt.getBaseTime(),
					DateTimeFormatter.ofPattern("yyyyMMddHHmm")
			);
		} catch (Exception e) {
			return null;
		}
	}
}
