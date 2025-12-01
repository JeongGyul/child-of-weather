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

    // ===== member_activities 관련 쿼리 =====
    private final String INSERT_MEMBER_ACTIVITY =
            "INSERT INTO member_activities (" +
                    "member_id, activity_type_id, " +
                    "min_temperature, max_temperature, " +
                    "max_humidity, max_precipitation, " +
                    "timing_status" +
            ") VALUES (?, ?, ?, ?, ?, ?, 'PLANNED')";

    private final String SELECT_MEMBER_ACTIVITIES_BY_MEMBER =
            "SELECT " +
                    "member_activity_id, member_id, activity_type_id, " +
                    "min_temperature, max_temperature, " +
                    "max_humidity, max_precipitation, " +
                    "recommendation_date, " +
                    "recommendation_start_time, recommendation_end_time, " +
                    "timing_status, " +
                    "last_calculated_at, created_at, updated_at " +
            "FROM member_activities " +
            "WHERE member_id = ? " +
            "ORDER BY created_at DESC";

    // 삭제용 쿼리
    private final String DELETE_MEMBER_ACTIVITY =
            "DELETE FROM member_activities " +
            "WHERE member_activity_id = ? AND member_id = ?";

    // ================= 로그인, 회원 관리 =====================

    public boolean login(MemberDTO mdto) {
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

    public int insert(MemberDTO mdto) {
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
            if (rs.next()) {
                infoDTO.setName(rs.getString("name"));
                infoDTO.setEmail(rs.getString("email"));
                infoDTO.setPassword(rs.getString("password"));
                infoDTO.setRole(rs.getString("role"));
                infoDTO.setCreatedAt(rs.getObject("created_at", LocalDate.class));
                infoDTO.setLastLoginAt(rs.getObject("last_login_at", LocalDate.class));
                infoDTO.setMemberId(rs.getLong("member_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return infoDTO;
    }

    public int updateProfile(String originalEmail, MemberDTO mdto) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(USER_UPDATE_PROFILE);
            pstmt.setString(1, mdto.getName());
            pstmt.setString(2, mdto.getEmail());
            pstmt.setString(3, originalEmail);

            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt);
        }
        return result;
    }

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

    public int deleteMember(String memberId) {
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
        return result;
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

    // ================== member_activities =====================

    public int insertMemberActivity(MemberDTO.MemberActivity activity) {
        int result = 0;
        try {
            // 필수 FK null 방지
            if (activity.getActivityTypeId() == null) {
                throw new IllegalArgumentException("activityTypeId is required");
            }
            if (activity.getMemberId() == null) {
                throw new IllegalArgumentException("memberId is required");
            }

            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(INSERT_MEMBER_ACTIVITY);

            // PK/FK는 null 허용 안 함
            pstmt.setLong(1, activity.getMemberId());
            pstmt.setLong(2, activity.getActivityTypeId());

            // Nullable 컬럼들
            if (activity.getMinTemperature() != null) {
                pstmt.setInt(3, activity.getMinTemperature());
            } else {
                pstmt.setNull(3, Types.TINYINT);
            }

            if (activity.getMaxTemperature() != null) {
                pstmt.setInt(4, activity.getMaxTemperature());
            } else {
                pstmt.setNull(4, Types.TINYINT);
            }

            if (activity.getMaxHumidity() != null) {
                pstmt.setInt(5, activity.getMaxHumidity());
            } else {
                pstmt.setNull(5, Types.TINYINT);
            }

            if (activity.getMaxPrecipitation() != null) {
                pstmt.setInt(6, activity.getMaxPrecipitation());
            } else {
                pstmt.setNull(6, Types.TINYINT);
            }

            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return result;
    }

    public List<MemberDTO.MemberActivity> getActivitiesByMemberId(Long memberId) {
        List<MemberDTO.MemberActivity> list = new ArrayList<>();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(SELECT_MEMBER_ACTIVITIES_BY_MEMBER);
            pstmt.setLong(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberDTO.MemberActivity a = new MemberDTO.MemberActivity();
                a.setMemberActivityId(rs.getLong("member_activity_id"));
                a.setMemberId(rs.getLong("member_id"));
                a.setActivityTypeId(rs.getLong("activity_type_id"));

                int minT = rs.getInt("min_temperature");
                a.setMinTemperature(rs.wasNull() ? null : minT);

                int maxT = rs.getInt("max_temperature");
                a.setMaxTemperature(rs.wasNull() ? null : maxT);

                int maxH = rs.getInt("max_humidity");
                a.setMaxHumidity(rs.wasNull() ? null : maxH);

                int maxP = rs.getInt("max_precipitation");
                a.setMaxPrecipitation(rs.wasNull() ? null : maxP);

                Date recDate = rs.getDate("recommendation_date");
                if (recDate != null) {
                    a.setRecommendationDate(recDate.toLocalDate());
                }

                Time startTime = rs.getTime("recommendation_start_time");
                if (startTime != null) {
                    a.setRecommendationStartTime(startTime.toLocalTime());
                }

                Time endTime = rs.getTime("recommendation_end_time");
                if (endTime != null) {
                    a.setRecommendationEndTime(endTime.toLocalTime());
                }

                a.setTimingStatus(rs.getString("timing_status"));

                Timestamp lastCalc = rs.getTimestamp("last_calculated_at");
                if (lastCalc != null) {
                    a.setLastCalculatedAt(lastCalc.toLocalDateTime());
                }

                Timestamp created = rs.getTimestamp("created_at");
                if (created != null) {
                    a.setCreatedAt(created.toLocalDateTime());
                }

                Timestamp updated = rs.getTimestamp("updated_at");
                if (updated != null) {
                    a.setUpdatedAt(updated.toLocalDateTime());
                }

                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public int deleteMemberActivity(long memberActivityId, long memberId) {
        int result = 0;
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(DELETE_MEMBER_ACTIVITY);
            pstmt.setLong(1, memberActivityId);
            pstmt.setLong(2, memberId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return result;
    }
}
