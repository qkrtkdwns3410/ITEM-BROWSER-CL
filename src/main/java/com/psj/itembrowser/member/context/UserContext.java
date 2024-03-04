package com.psj.itembrowser.member.context;

import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 *packageName    : com.psj.itembrowser.member.context
 * fileName       : UserContext
 * author         : ipeac
 * date           : 2024-03-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-04        ipeac       최초 생성
 */
@Slf4j
public class UserContext {
	private static final ThreadLocal<UserDetailsServiceImpl.CustomUserDetails> currentUser = new ThreadLocal<>();
	
	public static UserDetailsServiceImpl.CustomUserDetails getCurrentUser() {
		log.info("UserContext getCurrentUser");
		
		UserDetailsServiceImpl.CustomUserDetails current = currentUser.get();
		
		if (current == null) {
			log.error("UserContext is null");
		}
		
		return current;
	}
	
	public static void setCurrentUser(UserDetailsServiceImpl.CustomUserDetails user) {
		log.info("UserContext setCurrentUser");
		
		if (user == null) {
			log.error("UserContext is null");
			
			clear();
			
			return;
		}
		
		currentUser.set(user);
	}
	
	public static void clear() {
		log.info("UserContext clear");
		
		currentUser.remove();
	}
}