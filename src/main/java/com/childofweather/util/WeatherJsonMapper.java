package com.childofweather.util;

import com.childofweather.dto.WeatherDTO;

import java.util.List;

public class WeatherJsonMapper {

    private String escape(String s) {
        if(s == null) return "";
        return s.replace("\"", "\\\"");
    }

    public String toJson(WeatherDTO.Response dto) {
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

        sb.append("}");

        return sb.toString();
    }






}
