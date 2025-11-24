package com.childofweather.dao;

import com.childofweather.dto.MemberDTO;
import com.childofweather.util.JdbcConnectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // 로그인
    private final String USER_LOGIN =
            "SELECT * FROM members WHERE email = ? AND password = ?";

    // 회원가입
    private final String USER_INSERT =
            "INSERT INTO members(name, email, password) VALUES (?, ?, ?)";

    public boolean login(MemberDTO mdto) {
        boolean result = false;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, mdto.getEmail());
            pstmt.setString(2, mdto.getPassword());
            rs = pstmt.executeQuery();

            result = rs.next(); // 한 행이라도 있으면 true
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return result;
    }

    public int insert(MemberDTO mdto) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_INSERT);
            pstmt.setString(1, mdto.getName());
            pstmt.setString(2, mdto.getEmail());
            pstmt.setString(3, mdto.getPassword());

            result = pstmt.executeUpdate(); // 1이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	JdbcConnectUtil.close(conn, pstmt);
        }
        return result;
    }
    
    public MemberDTO getMember(MemberDTO loginDTO) {
    	MemberDTO infoDTO = new MemberDTO();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, loginDTO.getEmail());
            pstmt.setString(2, loginDTO.getPassword());
            rs = pstmt.executeQuery();
            rs.next();
            
            infoDTO.setName(rs.getString("name"));
            infoDTO.setEmail(rs.getString("email"));
            infoDTO.setPassword(rs.getString("password"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return infoDTO;
    }
}
