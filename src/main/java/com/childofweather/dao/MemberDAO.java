package com.childofweather.dao;

import com.childofweather.dto.MemberDTO;
import com.childofweather.util.JdbcConnectUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    // 프로필 수정 (이름 + 이메일) - 기존 이메일 기준으로 수정
    private final String USER_UPDATE_PROFILE =
            "UPDATE members SET name = ?, email = ? WHERE email = ?";

    // 전체 회원 목록 조회
    private final String USER_FIND_ALL =
            "SELECT member_id, name, email, role, created_at, last_login_at " +
            "FROM members ORDER BY member_id";

    // 회원 삭제
    private final String USER_DELETE=
            "DELETE FROM members WHERE member_id = ?";

    private final String USER_COUNT =
            "SELECT COUNT(*) AS cnt FROM members WHERE role = 'USER'";

    private final String ADMIN_COUNT =
            "SELECT COUNT(*) AS cnt FROM members WHERE role = 'ADMIN'";

    private final String NEW_USER_COUNT =
            "SELECT COUNT(*) AS cnt FROM members " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)";

    private final String LAST_LOGIN =
            "UPDATE members SET last_login_at = NOW() WHERE email = ?";

    private final String ACTIVE_USER_COUNT =
            "SELECT COUNT(*) AS cnt FROM members " +
            "WHERE last_login_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
    
    private final String USER_ACTIVITY_COUNT =
            "SELECT COUNT(*) AS cnt FROM member_activities " +
            "WHERE member_id = ?";


    // ================= 로그인, 회원 관리 =====================

    public boolean login(MemberDTO.LoginRequest mdto) {
        boolean result = false;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, mdto.getEmail());
            pstmt.setString(2, mdto.getPassword());
            rs = pstmt.executeQuery();

            result = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return result;
    }

    public Boolean insert(MemberDTO.JoinRequest mdto) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_INSERT);
            pstmt.setString(1, mdto.getName());
            pstmt.setString(2, mdto.getEmail());
            pstmt.setString(3, mdto.getPassword());

            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt);
        }
        return result == 1;
    }

    public MemberDTO.InfoResponse getMember(MemberDTO.LoginRequest dto) {
        MemberDTO.InfoResponse infoDTO = new MemberDTO.InfoResponse();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, dto.getEmail());
            pstmt.setString(2, dto.getPassword());
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	infoDTO.setMemberId(rs.getLong("member_id"));
            	infoDTO.setEmail(rs.getString("email"));
                infoDTO.setName(rs.getString("name"));
                infoDTO.setRole(rs.getString("role"));
                infoDTO.setCreatedAt(rs.getObject("created_at", LocalDate.class));
                infoDTO.setLastLoginAt(rs.getObject("last_login_at", LocalDate.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return infoDTO;
    }

    public int updateProfile(String originalEmail, MemberDTO.MyPageEditRequest dto) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_UPDATE_PROFILE);
            pstmt.setString(1, dto.getName());
            pstmt.setString(2, dto.getEmail());
            pstmt.setString(3, originalEmail);

            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt);
        }
        return result;
    }

    public List<MemberDTO.InfoResponse> getAllMembers() {
        List<MemberDTO.InfoResponse> list = new ArrayList<>();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_FIND_ALL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberDTO.InfoResponse dto = new MemberDTO.InfoResponse();
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role"));
                dto.setMemberId(rs.getLong("member_id"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDate.class));
                dto.setLastLoginAt(rs.getObject("last_login_at", LocalDate.class));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public void updateLastLogin(String email) {
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(LAST_LOGIN);
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
    }

    public Boolean deleteMember(String memberId) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_DELETE);
            pstmt.setString(1, memberId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return result == 1;
    }

    public int getUserCount() {
        int count = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_COUNT);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    public int getAdminCount() {
        int count = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(ADMIN_COUNT);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    public int getNewUserCount() {
        int count = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(NEW_USER_COUNT);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    public int getActiveUserCount() {
        int count = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(ACTIVE_USER_COUNT);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return count;
    }
    
    public int getUserActivityCount(String memberId) {
        int count = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_ACTIVITY_COUNT);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return count;
    }
}
