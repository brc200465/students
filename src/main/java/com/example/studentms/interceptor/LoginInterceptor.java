package com.example.studentms.interceptor;

import com.example.studentms.util.JwtUtil;
import tools.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor{

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception{

            String authorization=request.getHeader("Authorization");

            if(authorization==null||!authorization.startsWith("Bearer")){
                writeUnauthorized(response);
                return false;
            }

            String token=authorization.substring(7);

            try{
                Claims claims=jwtUtil.parseToken(token);

                request.setAttribute("loginUserId",claims.get("userId"));
                request.setAttribute("loginUsername",claims.get("username"));

                return true;
            }catch(Exception e){
                writeUnauthorized(response);
                return false;
            }
        }

        private void writeUnauthorized(HttpServletResponse response) throws Exception{
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");

            Map<String,Object>result=new HashMap<>();
            result.put("code",401);
            result.put("message","请先登录");
            result.put("data",null);

            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
}
