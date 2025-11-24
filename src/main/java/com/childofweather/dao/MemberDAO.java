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
            "SELECT * FROM membertbl WHERE memberid = ? AND password = ?";

    // 회원가입
    private final String USER_INSERT =
            "INSERT INTO membertbl(memberid, password, name, email) VALUES (?, ?, ?, ?)";

    public boolean login(MemberDTO mdto) {
        boolean result = false;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, mdto.getMemberid());
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
            pstmt.setString(1, mdto.getMemberid());
            pstmt.setString(2, mdto.getPassword());
            pstmt.setString(3, mdto.getName());
            pstmt.setString(4, mdto.getEmail());

            result = pstmt.executeUpdate(); // 1이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectU
