package com.psj.itembrowser.security.common;

import java.time.LocalDateTime;

import javax.persistence.Column;

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
	@Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	protected LocalDateTime createdDate;
	@Column(name = "updated_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	protected LocalDateTime updatedDate;
	@Column(name = "deleted_date", columnDefinition = "TIMESTAMP")
	protected LocalDateTime deletedDate;
}