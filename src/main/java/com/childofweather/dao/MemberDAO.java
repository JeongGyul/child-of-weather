package com.childofweather.dao;

import com.childofweather.dto.MemberDTO;
import com.childofweather.util.JdbcConnectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // ===== 관리자용 쿼리 =====

    // 전체 회원 수
    private final String USER_COUNT =
            "SELECT COUNT(*) AS cnt FROM members";

    // 전체 회원 목록 조회 (필요한 컬럼만)
    // ✅ role 컬럼까지 함께 조회
    private final String USER_FIND_ALL =
            "SELECT name, email, role FROM members ORDER BY member_id DESC";

    // 회원 삭제 (email 기준)
    private final String USER_DELETE_BY_EMAIL =
            "DELETE FROM members WHERE email = ?";

    // ==========================
    // 로그인 (true/false)
    // ==========================
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

    // ==========================
    // 회원가입
    // ==========================
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

    // ==========================
    // 로그인한 회원 상세 정보 가져오기
    // ==========================
    public MemberDTO getMember(MemberDTO loginDTO) {
        MemberDTO infoDTO = new MemberDTO();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_LOGIN);
            pstmt.setString(1, loginDTO.getEmail());
            pstmt.setString(2, loginDTO.getPassword());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                infoDTO.setName(rs.getString("name"));
                infoDTO.setEmail(rs.getString("email"));
                infoDTO.setPassword(rs.getString("password"));
                infoDTO.setRole(rs.getString("role")); // ✅ role 세팅
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return infoDTO;
    }

    // ==========================
    // 마이페이지: 프로필 수정 (이름 + 이메일)
    // ==========================
    public int updateProfile(String originalEmail, MemberDTO mdto) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_UPDATE_PROFILE);
            pstmt.setString(1, mdto.getName());   // 새 이름
            pstmt.setString(2, mdto.getEmail());  // 새 이메일
            pstmt.setString(3, originalEmail);    // WHERE email = 기존 이메일

            result = pstmt.executeUpdate();       // 1이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt);
        }
        return result;
    }

    // ==========================
    // 관리자: 전체 회원 수
    // ==========================
    public int getTotalMemberCount() {
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

    // ==========================
    // 관리자: 전체 회원 목록 조회
    // ==========================
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> list = new ArrayList<>();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_FIND_ALL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberDTO dto = new MemberDTO();
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role")); // ✅ role 함께 가져오기
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    // ==========================
    // 관리자: 이메일로 회원 삭제
    // ==========================
    public int deleteByEmail(String email) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_DELETE_BY_EMAIL);
            pstmt.setString(1, email);
            result = pstmt.executeUpdate();   // 1이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt);
        }
        return result;
    }
}
