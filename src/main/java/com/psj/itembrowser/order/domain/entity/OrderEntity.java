package com.psj.itembrowser.order.domain.entity;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.state.Cancelable;
import com.psj.itembrowser.order.domain.vo.Order;
import com.psj.itembrowser.order.domain.vo.OrderCalculationResult;
import com.psj.itembrowser.order.domain.vo.OrderStatus;
import com.psj.itembrowser.order.domain.vo.PaymentStatus;
import com.psj.itembrowser.security.common.BaseDateTimeEntity;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.common.exception.NotFoundException;
import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseDateTimeEntity implements Cancelable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", length = 200)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 200)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "PAID_DATE")
    private LocalDateTime paidDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_INFO_ID", referencedColumnName = "ID")
    private ShippingInfoEntity shippingInfo;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERER_NUMBER", referencedColumnName = "MEMBER_NO")
    private MemberEntity member;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrdersProductRelationEntity> ordersProductRelations = new ArrayList<>();
    
    @Transient
    private OrderCalculationResult orderCalculationResult;
    
    @Builder
    private OrderEntity(Long id, OrderStatus orderStatus, PaymentStatus paymentStatus, LocalDateTime paidDate, ShippingInfoEntity shippingInfo,
                        MemberEntity member, List<OrdersProductRelationEntity> ordersProductRelations, OrderCalculationResult orderCalculationResult,
                        LocalDateTime deletedDate) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.paidDate = paidDate;
        this.shippingInfo = shippingInfo;
        this.member = member;
        this.ordersProductRelations = ordersProductRelations == null ? new ArrayList<>() : ordersProductRelations;
        this.orderCalculationResult = orderCalculationResult;
        super.deletedDate = deletedDate;
    }
    
    public static OrderEntity from(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        
        orderEntity.id = order.getId();
        orderEntity.orderStatus = order.getOrderStatus();
        orderEntity.paidDate = order.getPaidDate();
        orderEntity.shippingInfo = ShippingInfoEntity.from(order.getShippingInfo());
        orderEntity.createdDate = order.getCreatedDate();
        orderEntity.updatedDate = order.getUpdatedDate();
        orderEntity.deletedDate = order.getDeletedDate();
        orderEntity.member = MemberEntity.from(order.getMember(), null);
        orderEntity.ordersProductRelations = order.getProducts().stream()
                .map(OrdersProductRelationEntity::from)
                .collect(Collectors.toList());
        
        return orderEntity;
    }
    
    public static OrderEntity from(OrderCreateRequestDTO dto, OrderCalculationResult orderCalculationResult) {
        if (dto == null) {
            throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
        }
        
        if (orderCalculationResult == null) {
            throw new NotFoundException(ErrorCode.ORDER_CALCULATION_RESULT_NOT_FOUND);
        }
        
        return OrderEntity.builder()
                .orderStatus(OrderStatus.PENDING)
                .shippingInfo(ShippingInfoEntity.from(dto.getShippingInfo()))
                .ordersProductRelations(dto.getProducts().stream()
                        .map(OrdersProductRelationEntity::from)
                        .collect(Collectors.toList()))
                .member(MemberEntity.from(dto.getMember()))
                .orderCalculationResult(orderCalculationResult)
                .build();
        
    }
    
    public void completePayment() {
        if (this.paymentStatus == PaymentStatus.COMPLETE) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETE_PAYMENT);
        }
        
        this.paymentStatus = PaymentStatus.COMPLETE;
        this.paidDate = LocalDateTime.now();
    }
    
    @Override
    public boolean isNotCancelable() {
        List<OrderStatus> cancelableStatus = List.of(OrderStatus.PENDING, OrderStatus.ACCEPT, OrderStatus.INSTRUCT);
        
        return cancelableStatus.stream()
                .noneMatch(orderStatus::equals);
    }
    
    public void cancel() {
        if (isNotCancelable()) {
            throw new BadRequestException(ErrorCode.ORDER_NOT_CANCELABLE);
        }
        
        this.orderStatus = OrderStatus.CANCELED;
        this.deletedDate = LocalDateTime.now();
        
        cancelOrderProducts();
    }
    
    private void cancelOrderProducts() {
        if (CollectionUtils.isEmpty(this.ordersProductRelations)) {
            return;
        }
        
        for (OrdersProductRelationEntity ordersProductRelation : this.ordersProductRelations) {
            ordersProductRelation.cancel();
        }
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                        o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        OrderEntity that = (OrderEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}