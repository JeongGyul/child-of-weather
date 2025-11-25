package com.childofweather.controller;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin.do")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // (선택) 관리자 권한 체크
        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session == null)
                ? null
                : (MemberDTO) session.getAttribute("loginUser");

        // 일단 role 기반으로 체크 (role이 ADMIN인 경우만 통과)
        if (loginUser == null || !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            // 관리자 아니면 대시보드로 돌려보내기
            response.sendRedirect(request.getContextPath() + "/dashboard.do");
            return;
        }

        MemberDAO dao = new MemberDAO();

        // 1) 전체 회원 수
        int totalMemberCount = dao.getTotalMemberCount();

        // 2) 신규 가입 / 활성 사용자 (더미)
        int newUserCount = 3;
        int activeUserCount = 12;

        // 3) 전체 회원 목록
        List<MemberDTO> memberList = dao.getAllMembers();

        // JSP에서 사용할 값 세팅
        request.setAttribute("totalMemberCount", totalMemberCount);
        request.setAttribute("newUserCount", newUserCount);
        request.setAttribute("activeUserCount", activeUserCount);
        request.setAttribute("memberList", memberList);

        // 관리자 페이지 JSP로 포워드
        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        if (email != null && !email.isEmpty()) {
            MemberDAO dao = new MemberDAO();
            dao.deleteByEmail(email);
        }

        // 삭제 후 다시 관리자 페이지로
        response.sendRedirect(request.getContextPath() + "/admin.do");
    }
}
