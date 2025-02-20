package com.example.demo.Configuration;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.ServicePackage.JwtService;
import com.example.demo.ServicePackage.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// This class is to validate the token for every API.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtService jwtService;
	private final UserService userService;
	
	public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)		//filterChain has the conditions need to be applied on the request API
			throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization"); //getting the header
		final String jwt; //we store tocken here
		final String userEmail;
		
		if(io.micrometer.common.util.StringUtils.isEmpty(authHeader) ||
				!org.apache.commons.lang3.StringUtils.startsWith(authHeader,"Bearer")) {
			
			filterChain.doFilter(request, response);
			return ;
			
		}
		
		jwt= authHeader.substring(7);//getting token from header
		userEmail = jwtService.extractUserName(jwt); // to extract email we send tocken to extractUserName method to 
		
		if(org.apache.commons.lang3.StringUtils.isNoneEmpty(userEmail) &&
						SecurityContextHolder.getContext().getAuthentication() == null)
		{
			UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
			
			if(jwtService.isTokenValid(jwt, userDetails)) {
				
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
						
						userDetails, null, userDetails.getAuthorities()
						);
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				securityContext.setAuthentication(token);
				SecurityContextHolder.setContext(securityContext);
			}
			
		}
		filterChain.doFilter(request, response);
		
	}

}
