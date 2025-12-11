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
        public void buildCondition() {
            // 조건 텍스트
            switch (pty) {
                case 0:
                    if (rain1h > 0.0) {
                        this.conditionText = "약한 비";
                    } else {
                        this.conditionText = "맑음";
                    }
                    break;
                case 1:
                    this.conditionText = "비";
                    break;
                case 2:
                    this.conditionText = "비/눈";
                    break;
                case 3:
                    this.conditionText = "눈";
                    break;
                case 4:
                    this.conditionText = "소나기";
                    break;
                default:
                    this.conditionText = "알 수 없음";
            }

            // 강수확률(간단 로직)
            this.precipitationProb = (rain1h > 0.0) ? 100.0 : 0.0;

            // 상태 메시지
            if (pty == 0) {
                if (!Double.isNaN(temperature) && temperature >= 28) {
                    this.statusMessage = "덥고 습한 날씨입니다. 수분 보충과 실내 휴식을 추천합니다.";
                } else if (!Double.isNaN(temperature) && temperature <= 3) {
                    this.statusMessage = "기온이 낮습니다. 따뜻한 옷차림을 준비하세요.";
                } else if (!Double.isNaN(humidity) && humidity >= 80) {
                    this.statusMessage = "비는 없지만 습도가 높습니다. 실내 공기 환기에 신경 써주세요.";
                } else {
                    this.statusMessage = "오늘도 맑고 쾌적한 하루가 예상됩니다.";
                }
            } else {
                switch (pty) {
                    case 1:
                        this.statusMessage = "비가 내리고 있습니다. 우산을 챙기고 이동 시간에 여유를 두세요.";
                        break;
                    case 2:
                        this.statusMessage = "비와 눈이 섞여 내리고 있습니다. 도로가 미끄러울 수 있습니다.";
                        break;
                    case 3:
                        this.statusMessage = "눈이 내리고 있습니다. 미끄럼 사고에 주의하세요.";
                        break;
                    case 4:
                        this.statusMessage = "소나기가 지나는 중입니다. 짧은 시간 강한 비에 대비하세요.";
                        break;
                    default:
                        this.statusMessage = "강수 형태가 불안정합니다. 최신 기상 정보를 확인하세요.";
                        break;
                }
            }
        }
    }

    // ========== 시간별 예보 ==========
    public static class HourlyForecast {
        private String time;          // "09:00"
        private double temperature = Double.NaN;
        private int pty = 0;
        private int pop = -1;

        public HourlyForecast(String time) { this.time = time; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public int getPty() { return pty; }
        public void setPty(int pty) { this.pty = pty; }
        public int getPop() { return pop; }
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

        private String escape(String s) {
            if (s == null) return "";
            return s.replace("\"", "\\\"");
        }


        @Override
        public String toString() {
            return "WeatherDTO.Response(locationName=" + locationName +
                    ", current=" + current +
                    ", hourly(size)=" + hourly.size() + ")";
        }
    }
}