package com.psj.itembrowser.security.common.interceptor.pre;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.psj.itembrowser.security.common.config.jwt.JwtProvider;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 *packageName    : com.psj.itembrowser.security.common.interceptor.pre
 * fileName       : UserInterceptor
 * author         : ipeac
 * date           : 2024-03-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-04        ipeac       최초 생성
 */
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {
	private final UserDetailsServiceImpl userDetailsService;
	private JwtProvider jwtProvider;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//jwt 토큰 추출
		String authorization = request.getHeader("Authorization");
		
		if (authorization != null && authorization.startsWith("Bearer ")) {
			String jwtToken = authorization.substring(7);
			
			UserDetailsServiceImpl.CustomUserDetails userDetails = userDetailsService.loadUserByJwt(jwt);
			
			//UserContext.setCurrentUser(userDetails);
		}
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}