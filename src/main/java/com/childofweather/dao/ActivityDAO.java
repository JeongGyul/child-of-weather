package com.childofweather.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.childofweather.dto.ActivityDTO;
import com.childofweather.util.JdbcConnectUtil;

import javax.xml.crypto.Data;

public class ActivityDAO {
	
	Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
	
    private final String INSERT_MEMBER_ACTIVITY =
            "INSERT INTO member_activities (" +
                    "member_id, activity_type_id, " +
                    "min_temperature, max_temperature, " +
                    "max_humidity, max_precipitation, " +
                    "timing_status" +
            ") VALUES (?, ?, ?, ?, ?, ?, 'PLANNED')";

    private final String SELECT_MEMBER_ACTIVITIES_BY_MEMBER =
            "SELECT " +
                    "ma.member_activity_id, ma.member_id, ma.activity_type_id, " +
                    "ma.min_temperature, ma.max_temperature, " +
                    "ma.max_humidity, ma.max_precipitation, " +
                    "ma.recommendation_date, " +
                    "ma.recommendation_start_time, ma.recommendation_end_time, " +
                    "ma.timing_status, " +
                    "ma.last_calculated_at, ma.created_at, ma.updated_at, " +
                    "at.default_duration_min " +
            "FROM member_activities ma " +
            "JOIN activity_types at ON ma.activity_type_id = at.activity_type_id " +
            "WHERE ma.member_id = ? " +
            "ORDER BY ma.created_at DESC";

    private final String DELETE_MEMBER_ACTIVITY =
            "DELETE FROM member_activities " +
            "WHERE member_activity_id = ? AND member_id = ?";

    private final String UPDATE_MEMBER_ACTIVITY_RECOMMENDATION =
            "UPDATE member_activities SET " +
                    "recommendation_date = ?, " +
                    "recommendation_start_time = ?, " +
                    "recommendation_end_time = ?, " +
                    "timing_status = ?, " +
                    "last_calculated_at = NOW(), " +
                    "updated_at = NOW() " +
            "WHERE member_activity_id = ? AND member_id = ?";


    private final String SELECT_RECOMMEND_ACTIVITY =
            "SELECT " +
                    "activity_type_id, name, default_duration_min, icon_code, is_active " +
            "FROM activity_types " +
            "WHERE is_active = 1";

    public void insertMemberActivity(ActivityDTO.Request dto) {
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(INSERT_MEMBER_ACTIVITY);

            pstmt.setLong(1, dto.getMemberId());
            pstmt.setLong(2, dto.getActivityTypeId());

            // Nullable 컬럼들
            if (dto.getMinTemperature() != null) {
                pstmt.setInt(3, dto.getMinTemperature());
            } else {
                pstmt.setNull(3, Types.TINYINT);
            }

            if (dto.getMaxTemperature() != null) {
                pstmt.setInt(4, dto.getMaxTemperature());
            } else {
                pstmt.setNull(4, Types.TINYINT);
            }

            if (dto.getMaxHumidity() != null) {
                pstmt.setInt(5, dto.getMaxHumidity());
            } else {
                pstmt.setNull(5, Types.TINYINT);
            }

            if (dto.getMaxPrecipitation() != null) {
                pstmt.setInt(6, dto.getMaxPrecipitation());
            } else {
                pstmt.setNull(6, Types.TINYINT);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
    }

    public List<ActivityDTO.Response> getActivities(Long memberId) {
        List<ActivityDTO.Response> list = new ArrayList<>();
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(SELECT_MEMBER_ACTIVITIES_BY_MEMBER);
            pstmt.setLong(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ActivityDTO.Response dto = new ActivityDTO.Response();
                dto.setMemberActivityId(rs.getLong("member_activity_id"));
                dto.setActivityTypeId(rs.getLong("activity_type_id"));
                dto.setDurationMin(rs.getInt("default_duration_min"));

                int minT = rs.getInt("min_temperature");
                dto.setMinTemperature(rs.wasNull() ? null : minT);

                int maxT = rs.getInt("max_temperature");
                dto.setMaxTemperature(rs.wasNull() ? null : maxT);

                int maxH = rs.getInt("max_humidity");
                dto.setMaxHumidity(rs.wasNull() ? null : maxH);

                int maxP = rs.getInt("max_precipitation");
                dto.setMaxPrecipitation(rs.wasNull() ? null : maxP);

                Date recDate = rs.getDate("recommendation_date");
                Time startTime = rs.getTime("recommendation_start_time");
                Time endTime = rs.getTime("recommendation_end_time");
                String timingStatus = rs.getString("timing_status");
                if(recDate != null) {
                    ActivityDTO.Recommendation recommendation = new ActivityDTO.Recommendation(
                            recDate.toLocalDate(),
                            startTime.toLocalTime(),
                            endTime.toLocalTime(),
                            timingStatus);
                    dto.setRecommendation(recommendation);
                }

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public void deleteMemberActivity(long memberActivityId, long memberId) {
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(DELETE_MEMBER_ACTIVITY);
            pstmt.setLong(1, memberActivityId);
            pstmt.setLong(2, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
    }

    public List<ActivityDTO.RecommendActivityResponse> getRecommendActivity() {
        List<ActivityDTO.RecommendActivityResponse> list = new ArrayList<>();

        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(SELECT_RECOMMEND_ACTIVITY);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ActivityDTO.RecommendActivityResponse dto = new ActivityDTO.RecommendActivityResponse();

                dto.setActivityTypeId(rs.getLong("activity_type_id"));
                dto.setActivityName(rs.getString("name"));
                dto.setDefaultDurationMin(rs.getInt("default_duration_min"));
                dto.setIconCode(rs.getString("icon_code"));

                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }

        return list;

    }


    public void updateRecommendation(Long memberActivityId,
                                     long memberId,
                                     LocalDate recDate,
                                     LocalTime startTime,
                                     LocalTime endTime,
                                     String timingStatus) {
        try {
            conn = JdbcConnectUtil.getConnection();
            pstmt = conn.prepareStatement(UPDATE_MEMBER_ACTIVITY_RECOMMENDATION);

            if (recDate != null) {
                pstmt.setDate(1, Date.valueOf(recDate));
            } else {
                pstmt.setNull(1, Types.DATE);
            }

            if (startTime != null) {
                pstmt.setTime(2, Time.valueOf(startTime));
            } else {
                pstmt.setNull(2, Types.TIME);
            }

            if (endTime != null) {
                pstmt.setTime(3, Time.valueOf(endTime));
            } else {
                pstmt.setNull(3, Types.TIME);
            }

            pstmt.setString(4, timingStatus);

            pstmt.setLong(5, memberActivityId);
            pstmt.setLong(6, memberId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(conn, pstmt, rs);
        }
    }
}
