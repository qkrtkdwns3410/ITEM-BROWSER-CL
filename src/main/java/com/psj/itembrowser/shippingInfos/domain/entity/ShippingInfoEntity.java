package com.psj.itembrowser.shippingInfos.domain.entity;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.dto.request.ShippingInfoRequestDTO;
import com.psj.itembrowser.shippingInfos.domain.vo.ShippingInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "shipping_infos")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ShippingInfoEntity extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo;
    
    @Size(max = 45)
    @Column(name = "RECEIVER", length = 45)
    private String receiver;
    
    @Size(max = 45)
    @Column(name = "MAIN_ADDRESS", length = 45)
    private String mainAddress;
    
    @Size(max = 45)
    @Column(name = "SUB_ADDRESS", length = 45)
    private String subAddress;
    
    @Size(max = 45)
    @Column(name = "PHONE_NUMBER", length = 45)
    private String phoneNumber;
    
    @Column(name = "ALTERNATIVE_NUMBER")
    private String alternativeNumber;
    
    @Size(max = 200)
    @Column(name = "SHIPPING_REQUEST_MSG", length = 200)
    private String shippingRequestMsg;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shippingInfo")
    private List<OrderEntity> orders = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", referencedColumnName = "MEMBER_NO", insertable = false, updatable = false)
    private MemberEntity member;
    
    @Builder
    private ShippingInfoEntity(Long id, Long memberNo, String receiver, String mainAddress, String subAddress, String phoneNumber,
                               String alternativeNumber, String shippingRequestMsg, LocalDateTime deletedDate,
                               List<OrderEntity> orders, MemberEntity member) {
        this.id = id;
        this.memberNo = memberNo;
        this.receiver = receiver;
        this.mainAddress = mainAddress;
        this.subAddress = subAddress;
        this.phoneNumber = phoneNumber;
        this.alternativeNumber = alternativeNumber;
        this.shippingRequestMsg = shippingRequestMsg;
        this.deletedDate = deletedDate;
        this.orders = orders == null ? new ArrayList<>() : orders;
        this.member = member;
    }
    
    public static ShippingInfoEntity from(ShippingInfo shippingInfo) {
        if (shippingInfo == null) {
            throw new NotFoundException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        
        return ShippingInfoEntity.builder()
                .id(shippingInfo.getId())
                .memberNo(shippingInfo.getMemberNo())
                .receiver(shippingInfo.getReceiver())
                .mainAddress(shippingInfo.getMainAddress())
                .subAddress(shippingInfo.getSubAddress())
                .phoneNumber(shippingInfo.getPhoneNumber())
                .alternativeNumber(shippingInfo.getAlternativeNumber())
                .shippingRequestMsg(shippingInfo.getShippingRequestMsg())
                .deletedDate(shippingInfo.getDeletedDate())
                .build();
    }
    
    public static ShippingInfoEntity from(ShippingInfoRequestDTO dto) {
        if (dto == null) {
            throw new NotFoundException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        
        return ShippingInfoEntity.builder()
                .id(dto.getId())
                .memberNo(dto.getMemberNo())
                .receiver(dto.getReceiver())
                .mainAddress(dto.getMainAddress())
                .subAddress(dto.getSubAddress())
                .phoneNumber(dto.getPhoneNumber())
                .alternativeNumber(dto.getAlternativeNumber())
                .shippingRequestMsg(dto.getShippingRequestMsg())
                .deletedDate(dto.getDeletedDate())
                .build();
    }
}