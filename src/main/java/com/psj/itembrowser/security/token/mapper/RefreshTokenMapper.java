package com.psj.itembrowser.security.token.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.psj.itembrowser.security.token.domain.vo.RefreshToken;

import lombok.NonNull;

@Mapper
public interface RefreshTokenMapper {

	/**
	 * refreshToken을 저장한다.
	 *
	 * @param refreshToken refreshToken
	 */
	Long createRefreshToken(RefreshToken refreshToken);

	/**
	 * refreshToken을 조회한다.
	 *
	 * @param memberNo memberNo
	 * @return refreshToken
	 */
	Optional<RefreshToken> getRefreshTokenByMemberNo(@NonNull Long memberNo);

	Optional<RefreshToken> getRefreshTokenByRefreshToken(@NonNull String refreshToken);
}