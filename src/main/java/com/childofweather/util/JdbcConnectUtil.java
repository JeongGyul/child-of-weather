package com.childofweather.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcConnectUtil {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ⚠️ 네 DB 환경에 맞게 수정
            String url = "jdbc:mysql://localhost:3306/project";
            String user = "root";
            String password = "0000";

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
