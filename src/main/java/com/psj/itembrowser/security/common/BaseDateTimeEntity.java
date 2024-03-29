package com.psj.itembrowser.security.common;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * packageName    : com.psj.itembrowser.common.domain
 * fileName       : BaseEntity
 * author         : ipeac
 * date           : 2023-10-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-11        ipeac       최초 생성
 */
@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDateTimeEntity {
	@CreatedDate
	@Column(name = "created_date", updatable = false)
	protected LocalDateTime createdDate;

	@LastModifiedDate
	@Column(name = "updated_date")
	protected LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	protected LocalDateTime deletedDate;
}