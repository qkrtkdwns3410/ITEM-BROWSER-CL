package com.psj.itembrowser.shippingInfos.domain.vo;

import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.dto.request.ShippingInfoRequestDTO;
import com.psj.itembrowser.shippingInfos.domain.dto.response.ShippingInfoResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo}
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingInfo {
    /**
     * 배송지 PK
     */
    private Long id;
    
    /**
     * 유저ID
     */
    private Long memberNo;
    
    /**
     * 수취인
     */
    private String receiver;
    
    /**
     * 메인주소
     */
    private String mainAddress;
    
    /**
     * 상세주소
     */
    private String subAddress;
    
    /**
     * 우편번호
     */
    private String zipCode;
    
    /**
     * 휴대폰 번호. 휴대폰번호 (-) 가 없어야함
     */
    private String phoneNumber;
    
    /**
     * 대안 연락처. 번호 (-) 가 없어야함
     */
    private String alternativeNumber;
    
    /**
     * 배송요청사항
     */
    private String shippingRequestMsg;
    
    /**
     * 생성일
     */
    private LocalDateTime createdDate;
    
    /**
     * 업데이트일
     */
    private LocalDateTime updatedDate;
    
    /**
     * 삭제일
     */
    private LocalDateTime deletedDate;
    
    @Builder
    private ShippingInfo(Long id, Long memberNo, String receiver, String mainAddress, String subAddress, String zipCode, String phoneNumber, String alternativeNumber, String shippingRequestMsg, LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
        this.id = id;
        this.memberNo = memberNo;
        this.receiver = receiver;
        this.mainAddress = mainAddress;
        this.subAddress = subAddress;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.alternativeNumber = alternativeNumber;
        this.shippingRequestMsg = shippingRequestMsg;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.deletedDate = deletedDate;
    }
    
    public static ShippingInfo from(ShippingInfoResponseDTO shippingInfoResponseDTO) {
        if (shippingInfoResponseDTO == null) {
            throw new NotFoundException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        ShippingInfo shippingInfo = new ShippingInfo();
        
        shippingInfo.id = shippingInfoResponseDTO.getId();
        shippingInfo.memberNo = shippingInfoResponseDTO.getMemberNo();
        shippingInfo.receiver = shippingInfoResponseDTO.getReceiver();
        shippingInfo.mainAddress = shippingInfoResponseDTO.getMainAddress();
        shippingInfo.subAddress = shippingInfoResponseDTO.getSubAddress();
        shippingInfo.phoneNumber = shippingInfoResponseDTO.getPhoneNumber();
        shippingInfo.alternativeNumber = shippingInfoResponseDTO.getAlternativeNumber();
        shippingInfo.shippingRequestMsg = shippingInfoResponseDTO.getShippingRequestMsg();
        shippingInfo.createdDate = shippingInfoResponseDTO.getCreatedDate();
        shippingInfo.updatedDate = shippingInfoResponseDTO.getUpdatedDate();
        shippingInfo.deletedDate = shippingInfoResponseDTO.getDeletedDate();
        
        return shippingInfo;
    }
    
    public static ShippingInfo from(ShippingInfoRequestDTO shippingInfo) {
        if (shippingInfo == null) {
            throw new NotFoundException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        ShippingInfo shippingInfoVO = new ShippingInfo();
        
        shippingInfoVO.id = shippingInfo.getId();
        shippingInfoVO.memberNo = shippingInfo.getMemberNo();
        shippingInfoVO.receiver = shippingInfo.getReceiver();
        shippingInfoVO.mainAddress = shippingInfo.getMainAddress();
        shippingInfoVO.subAddress = shippingInfo.getSubAddress();
        shippingInfoVO.phoneNumber = shippingInfo.getPhoneNumber();
        shippingInfoVO.alternativeNumber = shippingInfo.getAlternativeNumber();
        shippingInfoVO.shippingRequestMsg = shippingInfo.getShippingRequestMsg();
        shippingInfoVO.createdDate = shippingInfo.getCreatedDate();
        shippingInfoVO.updatedDate = shippingInfo.getUpdatedDate();
        shippingInfoVO.deletedDate = shippingInfo.getDeletedDate();
        
        return shippingInfoVO;
    }
}