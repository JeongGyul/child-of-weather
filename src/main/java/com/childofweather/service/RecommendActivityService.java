package com.childofweather.service;

import com.childofweather.dao.ActivityDAO;
import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.WeatherDTO;

import java.util.List;

public class RecommendActivityService {

    private static ActivityDAO activityDAO = new ActivityDAO();

    public static List<ActivityDTO.RecommendActivityResponse> getRecommendActivities(WeatherDTO.Response weather) {
        List<ActivityDTO.RecommendActivityResponse> activities = activityDAO.getRecommendActivity();

        for (int i = 0; i < activities.size(); i++) {

            if(activities.get(i).getActivityName().equals("빨래 건조") && weather.getCurrent().getHumidity() > 80) {
                activities.remove(i);
            }
            if(activities.get(i).getActivityName().equals("세차") && weather.getCurrent().getPty() > 0) {
                activities.remove(i);
            }

            if(activities.get(i).getActivityName().equals("실외 운동") && weather.getCurrent().getPty() > 0) {
                activities.remove(i);
            }

            if(activities.get(i).getActivityName().equals("정원 가꾸기") && weather.getCurrent().getPty() > 1) {
                activities.remove(i);
            }
        }
        return activities;
    };
}
