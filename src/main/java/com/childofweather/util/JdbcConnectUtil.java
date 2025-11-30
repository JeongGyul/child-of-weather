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

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ⚠️ 네 DB 환경에 맞게 수정
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
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
