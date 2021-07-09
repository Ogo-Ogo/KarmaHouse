package com.masa.karma_house.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Order(20)
public class JWTFilter implements Filter {

	private JWTUtil jwtUtil;
	CustomUserDetailsService userDetailsService;

	@Autowired
	public void setInjection(JWTUtil jwtUtil, CustomUserDetailsService userDetailsService){
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndpoint(request.getServletPath(), request.getMethod())) {
			String authorizationHeader = request.getHeader("Authorization");

			if (authorizationHeader == null) {
				authorizationHeader = request.getHeader("X-Token");
			}

			System.out.println(authorizationHeader);

			if (authorizationHeader == null) {
				response.resetBuffer();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setHeader("Content-Type", "application/json");
				response.getOutputStream().print("Token not sended");
				response.flushBuffer();
				return;
			}

			String username = null;
			String jwt = null;

			try {
				if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
					jwt = authorizationHeader.substring(7);
					username = jwtUtil.extractUsername(jwt);
				}
				else {
					jwt = authorizationHeader;
					username = jwtUtil.extractUsername(jwt);
					request.setAttribute("name", username);
				}
				if (username != null) {
					UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
					if (this.jwtUtil.validateToken(jwt, userDetails)) {

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());

						usernamePasswordAuthenticationToken
								.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
						System.out.println("setted auth " + SecurityContextHolder.getContext().getAuthentication());
					}
				}
			} catch (Exception e) {
				response.resetBuffer();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setHeader("Content-Type", "application/json");
				response.getOutputStream().print(e.getMessage());
				response.flushBuffer();
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndpoint(String path, String method) {
		boolean res = !path.matches("/account/authenticate")
				&& !path.matches("/account/register");
		return res;
	}
}