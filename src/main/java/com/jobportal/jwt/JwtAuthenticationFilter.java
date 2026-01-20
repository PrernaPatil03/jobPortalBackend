//package com.jobportal.jwt;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//
//public class JwtAuthenticationFilter  extends OncePerRequestFilter{
//	@Autowired
//	private  JwtHelper jwtHelper;
//	@Autowired
//	private UserDetailsService userDetailsService;
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		
//		String requestHeader = request.getHeader("Authorization");
//		String username =null;
//		String token =null;
//		if(requestHeader!=null && requestHeader.startsWith("Bearer")) {
//			token =requestHeader.substring(7);
//			try {
//				username=this.jwtHelper.getUsernameFromToken(token);
//			}
//			catch(IllegalArgumentException e) {
//				e.printStackTrace();
//			}
//			catch(ExpiredJwtException e) {
//				e.printStackTrace();
//			}
//			catch(MalformedJwtException e) {
//				e.printStackTrace();
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//		 if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
//			 UserDetails userDetails =this.userDetailsService.loadUserByUsername(username);
//			 Boolean validateToken =this.jwtHelper.validateToken(token,userDetails.getUsername());
//			 if(validateToken) {
//				 UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//				 authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				 SecurityContextHolder.getContext().setAuthentication(authentication);
//				 
//			 }
//		 }
//		 filterChain.doFilter(request, response);
//		
//	}
//
//}



package com.jobportal.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/users/verify")  
            || path.startsWith("/users/register") 
            || path.startsWith("/users/login") 
            || path.startsWith("/users/sendOtp")
            || path.startsWith("/users//changePass")
            || path.startsWith("/users/verifyOtp");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            System.out.println("Authorization Header: " + requestHeader);
            System.out.println("Extracted Token: " + token);

            try {
                username = this.jwtHelper.getUsernameFromToken(token);
                System.out.println("Extracted Username: " + username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
//            try {
//                username = this.jwtHelper.getUsernameFromToken(token);
//            } catch (IllegalArgumentException | MalformedJwtException e) {
//                // Log and continue, letting the next filter handle the unauthenticated state
//                System.out.println("Unable to get JWT Token or invalid JWT Token");
//            } catch (ExpiredJwtException e) {
//                // This is the key fix. Send a 401 Unauthorized response to the client.
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\": \"JWT Token has expired\"}");
//                return; // Stop the filter chain
//            } catch (Exception e) {
//                // Catch any other unexpected exceptions
//                System.out.println("An unexpected error occurred during JWT processing");
//            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails.getUsername());
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}