package com.childofweather.util;

import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.WeatherDTO;
import com.childofweather.service.RecommendActivityService;

import java.util.List;

public class WeatherJsonMapper {

    private String escape(String s) {
        if(s == null) return "";
        return s.replace("\"", "\\\"");
    }

    public String toJson(WeatherDTO.Response dto, List<ActivityDTO.RecommendActivityResponse> activities) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("\"locationName\":\"").append(escape(dto.getLocationName())).append("\"");
        sb.append(",\"temperature\":").append(dto.getCurrent().getTemperature());
        sb.append(",\"conditionText\":\"").append(escape(dto.getCurrent().getConditionText())).append("\"");
        sb.append(",\"statusMessage\":\"").append(escape(dto.getCurrent().getStatusMessage())).append("\"");
        sb.append(",\"humidity\":").append(dto.getCurrent().getHumidity());
        sb.append(",\"windSpeed\":").append(dto.getCurrent().getWindSpeed());
        sb.append(",\"precipitationProb\":").append((int) dto.getCurrent().getPrecipitationProb());

        sb.append(",\"hourly\":[");
        List<WeatherDTO.HourlyForecast> hourly = dto.getHourly();
        for (int i = 0; i < hourly.size(); i++) {
            WeatherDTO.HourlyForecast h = hourly.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"time\":\"").append(h.getTime()).append("\"");
            sb.append(",\"temperature\":").append(h.getTemperature());
            sb.append(",\"pty\":").append(h.getPty());
            sb.append(",\"pop\":").append(h.getPop());
            sb.append("}");
        }
        sb.append("]");

        sb.append(",\"recommendations\":[");
        for (int i = 0; i < activities.size(); i++) {
            ActivityDTO.RecommendActivityResponse a = activities.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"activity_type_id\":\"").append(a.getActivityTypeId()).append("\"");
            sb.append(",\"activity_name\":\"").append(escape(a.getActivityName())).append("\"");
            sb.append(",\"duration_time\":").append(a.getDefaultDurationTime());
            sb.append(",\"icon_code\":\"").append(escape(a.getIconCode())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        sb.append("}");


        return sb.toString();
    }






}
