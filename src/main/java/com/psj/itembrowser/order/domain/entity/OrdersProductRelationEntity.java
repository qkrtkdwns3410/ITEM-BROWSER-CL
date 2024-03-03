package com.psj.itembrowser.order.domain.entity;

import com.psj.itembrowser.order.domain.dto.request.OrdersProductRelationRequestDTO;
import com.psj.itembrowser.order.domain.vo.OrdersProductRelation;
import com.psj.itembrowser.product.domain.entity.ProductEntity;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@IdClass(OrdersProductRelationEntity.OrdersProductRelationEntityId.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders_product_relation")
public class OrdersProductRelationEntity extends BaseDateTimeEntity {
    
    @Id
    @Column(name = "GROUP_ID", nullable = false)
    private Long groupId;
    
    @Id
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;
    
    @Column(name = "PRODUCT_QUANTITY")
    private Integer productQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GROUP_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "ID")
    private OrderEntity order;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "ID")
    private ProductEntity product;
    
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OrdersProductRelationEntityId implements Serializable {
        private Long groupId;
        private Long productId;
    }
    
    @Builder
    private OrdersProductRelationEntity(Long groupId, Long productId, Integer productQuantity, LocalDateTime deletedDate, OrderEntity order,
                                        ProductEntity product) {
        this.groupId = groupId;
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.order = order;
        this.product = product;
        this.deletedDate = deletedDate;
    }
    
    public static OrdersProductRelationEntity from(OrdersProductRelation ordersProductRelation) {
        if (ordersProductRelation == null) {
            throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
        }
        
        return OrdersProductRelationEntity.builder()
                .groupId(ordersProductRelation.getGroupId())
                .productId(ordersProductRelation.getProductId())
                .productQuantity(ordersProductRelation.getProductQuantity())
                .product(ProductEntity.from(ordersProductRelation.getProduct()))
                .build();
    }
    
    public static OrdersProductRelationEntity from(OrdersProductRelationRequestDTO dto) {
        if (dto == null) {
            throw new NotFoundException(ErrorCode.ORDER_PRODUCTS_EMPTY);
        }
        
        return OrdersProductRelationEntity.builder()
                .groupId(dto.getGroupId())
                .productId(dto.getProductId())
                .productQuantity(dto.getProductQuantity())
                .product(ProductEntity.from(dto.getProductResponseDTO()))
                .build();
    }
    
    public void cancel() {
        this.deletedDate = LocalDateTime.now();
        
        if (this.product != null) {
            this.product.increaseStock(this.productQuantity);
        }
    }
}