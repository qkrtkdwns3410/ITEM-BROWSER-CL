package com.psj.itembrowser.security.data.config;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.member.domain.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByCredentialsEmail(String email);
}