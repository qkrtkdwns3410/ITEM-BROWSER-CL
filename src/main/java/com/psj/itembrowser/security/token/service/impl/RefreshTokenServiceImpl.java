package com.psj.itembrowser.security.token.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psj.itembrowser.security.token.domain.vo.RefreshToken;
import com.psj.itembrowser.security.token.mapper.RefreshTokenMapper;
import com.psj.itembrowser.security.token.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.psj.itembrowser.token.service.impl
 * fileName       : RefreshTokenServiceImpl
 * author         : ipeac
 * date           : 2024-01-16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-01-16        ipeac       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenMapper refreshTokenMapper;

	@Override
	public RefreshToken createRefreshToken(RefreshToken refreshToken) {
		refreshTokenMapper.createRefreshToken(refreshToken);
		return null;
	}

	@Override
	public RefreshToken getRefreshToken(long memberNo) {
		Optional<RefreshToken> refreshToken = refreshTokenMapper.getRefreshTokenByMemberNo(memberNo);
		return refreshToken.orElse(null);
	}
}