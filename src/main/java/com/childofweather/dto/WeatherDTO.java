package com.childofweather.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherDTO {

    // ========== GridPoint ==========
    public static class GridPoint {
        private final int nx;
        private final int ny;

        public GridPoint(int nx, int ny) {
            this.nx = nx;
            this.ny = ny;
        }

        public int getNx() {
            return nx;
        }

        public int getNy() {
            return ny;
        }
    }

    // ========== BaseDateTime ==========
    public static class BaseDateTime {
        private final String baseDate;
        private final String baseTime;

        public BaseDateTime(String baseDate, String baseTime) {
            this.baseDate = baseDate;
            this.baseTime = baseTime;
        }

        public String getBaseDate() {
            return baseDate;
        }
        public String getBaseTime() {
            return baseTime;
        }
    }

    // ========== 현재 날씨 데이터 ==========
    public static class WeatherData {
        private double temperature = Double.NaN;
        private double humidity = Double.NaN;
        private double windSpeed = Double.NaN;
        private double rain1h = 0.0;
        private int pty = 0;
        private double precipitationProb = 0.0;
        private String conditionText = "정보 없음";
        private String statusMessage = "현재 기상 정보를 불러왔습니다.";

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getHumidity() { return humidity; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
        public double getRain1h() { return rain1h; }
        public void setRain1h(double rain1h) { this.rain1h = rain1h; }
        public int getPty() { return pty; }
        public void setPty(int pty) { this.pty = pty; }
        public double getPrecipitationProb() { return precipitationProb; }
        public void setPrecipitationProb(double precipitationProb) { this.precipitationProb = precipitationProb; }
        public String getConditionText() { return conditionText; }
        public void setConditionText(String conditionText) { this.conditionText = conditionText; }
        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

        /**
         * 원래 WeatherServlet의 toConditionText + buildStatusMessage 로직 합친 메서드
         */
    }

    // ========== 시간별 예보 ==========
    public static class HourlyForecast {
        // fcstDate(yyyyMMdd) + fcstTime(HHmm)를 한 번에 보관
        private BaseDateTime baseDateTime;
        private Double temperature1h = Double.NaN; // TMP
        private Integer humidity = null;           // REH
        private Integer pty = 0;                   // PTY
        private Integer pop = -1;                  // POP

        public HourlyForecast(BaseDateTime baseDateTime) {
            this.baseDateTime = baseDateTime;
        }

        public BaseDateTime getBaseDateTime() { return baseDateTime; }
        public void setBaseDateTime(BaseDateTime baseDateTime) { this.baseDateTime = baseDateTime; }

        // 화면 표시용 "HH:mm"
        public String getTime() {
            if (baseDateTime == null) return "";
            String t = baseDateTime.getBaseTime();
            if (t == null || t.length() < 4) return "";
            return t.substring(0, 2) + ":" + t.substring(2, 4);
        }

        public Double getTemperature1h() { return temperature1h; }
        public void setTemperature1h(double temperature1h) { this.temperature1h = temperature1h; }
        public Integer getHumidity() { return humidity; }
        public void setHumidity(Integer humidity) { this.humidity = humidity; }
        public Integer getPty() { return pty; }
        public void setPty(int pty) { this.pty = pty; }
        public Integer getPop() { return pop; }
        public void setPop(int pop) { this.pop = pop; }
    }

    // ========== 최종 응답 DTO (Servlet에서 JSON으로 내려줄 객체) ==========
    public static class Response {
        private final String locationName;
        private final WeatherData current;
        private final List<HourlyForecast> hourly;

        public Response(String locationName, WeatherData current, List<HourlyForecast> hourly) {
            this.locationName = locationName;
            this.current = current;
            this.hourly = (hourly != null) ? hourly : new ArrayList<>();
        }

        public String getLocationName() { return locationName; }
        public WeatherData getCurrent() { return current; }

        public List<HourlyForecast> getHourly() { return hourly; }


        @Override
        public String toString() {
            return "WeatherDTO.Response(locationName=" + locationName +
                    ", current=" + current +
                    ", hourly(size)=" + hourly.size() + ")";
        }
    }
}