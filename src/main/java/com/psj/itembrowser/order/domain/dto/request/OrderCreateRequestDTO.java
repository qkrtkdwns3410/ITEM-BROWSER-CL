package com.psj.itembrowser.order.domain.dto.request;

import com.psj.itembrowser.member.domain.dto.request.MemberRequestDTO;
import com.psj.itembrowser.shippingInfos.domain.dto.request.ShippingInfoRequestDTO;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * DTO for {@link com.psj.itembrowser.order.domain.vo.Order}
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreateRequestDTO {
    @NotNull
    @Positive
    private Long ordererNumber;
    
    @NotEmpty
    private List<OrdersProductRelationRequestDTO> products;
    
    @NotNull
    private MemberRequestDTO member;
    
    @NotNull
    private ShippingInfoRequestDTO shippingInfo;
    
    @Builder
    private OrderCreateRequestDTO(Long ordererNumber, List<OrdersProductRelationRequestDTO> products, MemberRequestDTO member, ShippingInfoRequestDTO shippingInfo) {
        this.ordererNumber = ordererNumber;
        this.products = products;
        this.member = member;
        this.shippingInfo = shippingInfo;
    }
    
    @Override
    public String toString() {
        return "OrderCreateRequestDTO{" +
                "ordererNumber=" + ordererNumber +
                ", products=" + products +
                ", member=" + member +
                ", shippingInfo=" + shippingInfo +
                '}';
    }
}