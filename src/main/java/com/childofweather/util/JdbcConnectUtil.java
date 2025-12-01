package com.childofweather.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnectUtil {
	
	private static final Properties properties = new Properties();

	static {
	    try (InputStream input = JdbcConnectUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
	        if (input == null) {
	            // 파일을 찾지 못하면 에러 발생
	            System.out.println("Sorry, unable to find db.properties");
	            throw new FileNotFoundException("db.properties 파일을 찾을 수 없습니다.");
	        }
	        // db.properties 파일 내용을 로드
	        properties.load(input);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

    // db.properties 로딩
    static {
        try (InputStream input = JdbcConnectUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                throw new FileNotFoundException("db.properties 파일을 찾을 수 없습니다.");
            }

            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ✅ 이 메서드를 네가 보낸 코드로 교체한 최종 버전
    public static Connection getConnection() {
        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // db.properties에서 값 읽기
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            System.out.println("[DB] url=" + url + ", user=" + user); // 디버그용

            // DB 연결 시도
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC 드라이버를 찾을 수 없습니다.", e);
        } catch (SQLException e) {
            throw new RuntimeException("DB 연결에 실패했습니다. URL/아이디/비밀번호를 확인하세요.", e);
        }
    }

    public static void close(Connection con, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null)   con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null)    rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null)   con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
