package com.itms.product.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.itms.product.config.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		if (authHeader != null || authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		token = authHeader.substring(7);
		try {
			username = jwtService.extractUsername(token);
		} catch (IllegalArgumentException e) {
			logger.info("illegal Argument while fetching the username !");
			e.printStackTrace();
		} catch (ExpiredJwtException e) {
			logger.info("Given jwt token is expired !");
			e.printStackTrace();
		} catch (MalformedJwtException e) {
			logger.info("Some change has done in token ! Invaild tokenFO !");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			if (jwtService.validateToken(token)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
