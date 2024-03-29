package com.psj.itembrowser.member.persistence;

import org.springframework.stereotype.Component;

import com.psj.itembrowser.member.domain.dto.request.MemberSignUpRequestDTO;
import com.psj.itembrowser.member.domain.dto.response.MemberResponseDTO;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.mapper.MemberMapper;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.psj.itembrowser.common.security.model.service.impl
 * fileName       : MemberPersistance
 * author         : ipeac
 * date           : 2024-01-09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-01-09        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class MemberPersistance {
	private final MemberMapper memberMapper;

	public MemberResponseDTO findByEmail(@NonNull String email) {
		Member member = memberMapper.findByEmail(email);

		if (member == null) {
			throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		}

		return MemberResponseDTO.from(member);
	}

	public MemberResponseDTO findById(@NonNull Long id) {
		Member member = memberMapper.findById(id);

		if (member == null) {
			throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		}

		return MemberResponseDTO.from(member);
	}

	public Long insertMember(MemberSignUpRequestDTO requestDTO) {
		boolean notInserted = !memberMapper.insertMember(requestDTO);

		if (notInserted) {
			throw new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
		}

		return requestDTO.getMemberNo();
	}

}