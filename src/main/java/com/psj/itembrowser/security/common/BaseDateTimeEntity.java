package com.psj.itembrowser.security.common;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class BaseDateTimeEntity {
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	protected LocalDateTime createdDate;
	@CreationTimestamp
	@UpdateTimestamp
	@Column(name = "updated_date")
	protected LocalDateTime updatedDate;
	@Column(name = "deleted_date")
	protected LocalDateTime deletedDate;
}