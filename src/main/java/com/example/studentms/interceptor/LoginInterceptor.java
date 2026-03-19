package com.example.studentms.interceptor;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor{
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception{
            HttpSession session=request.getSession(false);

            if(session==null||session.getAttribute("loginUserId")==null){
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");

                Map<String,Object>result=new HashMap<>();
                result.put("code",401);
                result.put("message","请先登录");
                result.put("data",null);

                response.getWriter().write(objectMapper.writeValueAsString(result));
                return false;
            }
            return true;
        }
}
