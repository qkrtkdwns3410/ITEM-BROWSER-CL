package com.psj.itembrowser.shippingInfos.domain.dto.request;

import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link ShippingInfo}
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ShippingInfoRequestDTO {
	Long id;
	Long memberNo;
	String receiver;
	String mainAddress;
	String subAddress;
	String phoneNumber;
	String alternativeNumber;
	String shippingRequestMsg;
	LocalDateTime createdDate;
	LocalDateTime updatedDate;
	LocalDateTime deletedDate;
	
	@Builder
	private ShippingInfoRequestDTO(Long id, Long memberNo, String receiver, String mainAddress, String subAddress, String phoneNumber,
                                   String alternativeNumber, String shippingRequestMsg, LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
		this.id = id;
		this.memberNo = memberNo;
		this.receiver = receiver;
		this.mainAddress = mainAddress;
		this.subAddress = subAddress;
		this.phoneNumber = phoneNumber;
		this.alternativeNumber = alternativeNumber;
		this.shippingRequestMsg = shippingRequestMsg;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.deletedDate = deletedDate;
	}
	
	public static ShippingInfoRequestDTO from(ShippingInfoEntity entity) {
		if (entity == null) {
			throw new NotFoundException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
		}
		
		return ShippingInfoRequestDTO.builder()
			.id(entity.getId())
			.memberNo(entity.getMemberNo())
			.receiver(entity.getReceiver())
			.mainAddress(entity.getMainAddress())
			.subAddress(entity.getSubAddress())
			.phoneNumber(entity.getPhoneNumber())
			.alternativeNumber(entity.getAlternativeNumber())
			.shippingRequestMsg(entity.getShippingRequestMsg())
			.createdDate(entity.getCreatedDate())
			.updatedDate(entity.getUpdatedDate())
			.deletedDate(entity.getDeletedDate())
			.build();
	}
}