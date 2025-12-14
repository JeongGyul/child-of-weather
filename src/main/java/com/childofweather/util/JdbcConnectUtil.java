package com.childofweather.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnectUtil {
	
    // 🟢 [수정] ApiConfig의 공통 메서드를 사용하여 db.properties 로드
    private static final Properties properties = ApiConfig.load("db.properties");

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            
            // 필수 값 체크 로직이 필요하다면 여기에 추가 가능
            
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC 드라이버를 찾을 수 없습니다.", e);
        } catch (SQLException e) {
            throw new RuntimeException("DB 연결에 실패했습니다. URL/아이디/비밀번호를 확인하세요.", e);
        }
    }

    // close 메서드들은 기존 그대로 유지...
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