package com.childofweather.util;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    // application.properties 전용 저장소
    private static final Properties properties;

    static {
        // static 초기화 블록에서도 공통 메서드 재사용
        properties = load("application.properties");
    }

    /**
     * application.properties의 값을 가져오는 메서드 (기존 유지)
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * [New] 임의의 프로퍼티 파일을 로드하여 반환하는 공통 유틸 메서드
     * 예: Properties dbProps = ApiConfig.load("db.properties");
     */
    public static Properties load(String fileName) {
        Properties props = new Properties();
        try (InputStream input = ApiConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                System.err.println("🚨 [오류] " + fileName + " 파일을 찾을 수 없습니다.");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("🚨 [오류] " + fileName + " 로딩 중 예외 발생");
            e.printStackTrace();
        }
        return props;
    }
}