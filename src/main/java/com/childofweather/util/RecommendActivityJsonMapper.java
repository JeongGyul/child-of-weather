package com.childofweather.util;

import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.WeatherDTO;

import java.util.List;

public class RecommendActivityJsonMapper {

    private String escape(String s) {
        if(s == null) return "";
        return s.replace("\"", "\\\"");
    }

    public String toJson(List<ActivityDTO.RecommendActivityResponse> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("\"recommendations\": [");
        for (int i = 0; i < list.size(); i++) {
            ActivityDTO.RecommendActivityResponse response = list.get(i);

            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"activity_type_id\":\"").append(response.getActivityTypeId()).append("\"");
            sb.append(",\"activity_name\":").append(response.getActivityName());
            sb.append(",\"duration_time\":").append(response.getDefaultDurationTime());
            sb.append(",\"icon_code\":").append(response.getIconCode());
            sb.append("}");
        }
        sb.append("]");
        sb.append("}");

        return sb.toString();
    }
}
