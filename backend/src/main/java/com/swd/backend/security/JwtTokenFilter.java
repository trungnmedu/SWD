package com.swd.backend.security;

import com.google.gson.Gson;
import com.swd.backend.entity.AccountLoginEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.exception.NotLatestTokenResponse;
import com.swd.backend.service.AccountLoginService;
import com.swd.backend.utils.JwtTokenUtils;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    JwtTokenUtils jwtTokenUtils;
    AccountLoginService accountLoginService;
    Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        try {
            if (request.getRequestURI().equals("/api/v1/logout")) {
                accountLoginService.logoutByToken(token);
                response.setStatus(200);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print("{\"message\":\"Logout success\"}");
                out.flush();
                return;
            }

            AccountLoginEntity login = accountLoginService.findLoginByToken(token);
            if (login == null) {
                sendErrorResponse(response, 401, "Token not available.");
                return;
            }

            if (login.isLogout()) {
                sendErrorResponse(response, 401, "User logged out.");
                return;
            }
            Claims claims = jwtTokenUtils.deCodeToken(token);
            SecurityUserDetails securityUserDetails = SecurityUserDetails.builder()
                    .username(claims.getSubject())
                    .role((String) claims.get("role"))
                    .password(UUID.randomUUID().toString())
                    .build();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityUserDetails, null, securityUserDetails.getAuthorities());
            authenticationToken.setDetails(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            sendErrorResponse(response, 401, "Token invalid.");
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            sendErrorResponse(response, 500, "Server temp error.");
            return;
        }
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .build();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(gson.toJson(errorResponse));
        out.flush();
    }

    private void sendTokenLatest(HttpServletResponse response, int status, String token) throws IOException {
        response.setStatus(status);
        NotLatestTokenResponse notLatestTokenResponse = NotLatestTokenResponse.builder().token(token).build();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(gson.toJson(notLatestTokenResponse));
        out.flush();
    }
}
