package com.childofweather.controller.route;

import com.childofweather.util.ApiConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/route.do")
public class RouteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 🟢 [추가] application.properties에서 키를 읽어와 request 속성에 저장
        // (주의: application.properties에 'naver.map.client.id' 키가 있어야 함)
        String clientId = ApiConfig.get("naver.map.client.id");
        req.setAttribute("naverMapClientId", clientId);

        req.getRequestDispatcher("/WEB-INF/views/route/route.jsp")
           .forward(req, resp);
    }
}