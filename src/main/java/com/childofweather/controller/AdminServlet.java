package com.childofweather.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin.do")
public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 관리자 보안 체크 (생략 가능하나 필수)
        // ...

        MemberDAO dao = new MemberDAO();
       // List<MemberDTO> list = dao.getAllMembers(); // 전체 회원 가져오기
        List<MemberDTO> list = new ArrayList();
        // 2. 통계 계산 (리액트가 하던 걸 자바에서!)
        int totalUsers = list.size();
        int adminCount = 0;
        int userCount = 0;
        int activeUsers = 0; // 최근 7일 접속자

        // 날짜 계산용
        long now = System.currentTimeMillis();
        long sevenDaysMillis = 7L * 24 * 60 * 60 * 1000;

        for (MemberDTO m : list) {
            // 역할 카운트
            if ("ADMIN".equals(m.getRole())) adminCount++;
            else userCount++;

            // 활성 사용자 (마지막 로그인이 7일 이내)
            // m.getLastLoginAt()이 LocalDateTime이나 Date라고 가정
            // (DB에 데이터가 없으면 에러날 수 있으니 null 체크 필수)
            /* if (m.getLastLoginAt() != null) {
                // 날짜 비교 로직 구현 필요
                activeUsers++;
            }
            */
        }

        // 3. 최근 가입자 (리스트를 가입일 역순으로 정렬해서 상위 5개만 자르기 등)
        // (여기서는 단순 카운트만 보냄)

        // 4. 데이터 세팅
        request.setAttribute("userList", list);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("adminCount", adminCount);
        request.setAttribute("userCount", userCount);
        request.setAttribute("newJoinCount", 5); // 임시 데이터 (실제론 DB 쿼리로 '오늘 가입자' 조회)

        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp").forward(request, response);
    }
}