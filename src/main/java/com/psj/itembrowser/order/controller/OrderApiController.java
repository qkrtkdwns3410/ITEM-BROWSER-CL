package com.psj.itembrowser.order.controller;

import com.psj.itembrowser.member.annotation.CurrentUser;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Member;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.order.domain.dto.request.OrderCreateRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderExchageRequestDTO;
import com.psj.itembrowser.order.domain.dto.request.OrderPageRequestDTO;
import com.psj.itembrowser.order.domain.dto.response.OrderResponseDTO;
import com.psj.itembrowser.order.service.OrderService;
import com.psj.itembrowser.security.common.message.MessageDTO;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static java.text.MessageFormat.format;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderService orderService;
    private final UserDetailsServiceImpl userDetailsService;
    
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @PostAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_CUSTOMER') and returnObject.body.member.credentials.email == principal.username)")
    @GetMapping("/v1/api/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @PathVariable Long orderId,
            @CurrentUser Jwt jwt
    ) {
        log.info("getOrders orderId : {}", orderId);
        
        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);
        
        Member member = Member.from(customUserDetails.getMemberResponseDTO());
        
        OrderResponseDTO dto = getOrderResponseBasedOnRole(member, orderId);
        
        return ResponseEntity.ok(dto);
    }
    
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/v1/api/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO orderCreateRequestDTO,
            @CurrentUser Jwt jwt
    ) {
        log.info("createOrder orderCreateRequestDTO : {}", orderCreateRequestDTO);
        
        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);
        
        MemberEntity member = MemberEntity.from(customUserDetails.getMemberResponseDTO());
        
        OrderResponseDTO createdOrder = orderService.createOrder(member, orderCreateRequestDTO);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
        
        return ResponseEntity.created(location).build();
    }
    
    @PostMapping("/v1/api/orders/{orderId}/exchange")
    public ResponseEntity<OrderResponseDTO> exchangeOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderExchageRequestDTO orderExchageRequestDTO,
            @CurrentUser Jwt jwt
    ) {
        log.info("exchangeOrder orderId : {}", orderId);
        
        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);
        
        MemberEntity member = MemberEntity.from(customUserDetails.getMemberResponseDTO());
        
        OrderResponseDTO exchangedOrder = orderService.exchangeOrder(member, orderId, orderExchageRequestDTO);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exchangedOrder.getId())
                .toUri();
        
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/v1/api/orders/users/{userNumber}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @PathVariable Long userNumber,
            @Valid @ModelAttribute OrderPageRequestDTO orderPageRequestDTO,
            @CurrentUser Jwt jwt
    ) {
        log.info("getOrders userNumber : {}", userNumber);
        
        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);
        
        MemberEntity member = MemberEntity.from(customUserDetails.getMemberResponseDTO());
        
        Page<OrderResponseDTO> orderResponseDTOPage = getOrdersResponseBasedOnRole(member, orderPageRequestDTO);
        
        return ResponseEntity.ok(orderResponseDTOPage);
    }
    
    @DeleteMapping("/v1/api/orders/{orderId}")
    public MessageDTO removeOrder(@PathVariable Long orderId) {
        orderService.removeOrder(orderId);
        
        return new MessageDTO(format("Order record for {0} has been deleted.", orderId));
    }
    
    private OrderResponseDTO getOrderResponseBasedOnRole(Member member, Long orderId) {
        if (member.hasRole(Role.ROLE_ADMIN)) {
            return orderService.getOrderWithNoCondition(orderId);
        }
        
        return orderService.getOrderWithNotDeleted(orderId);
    }
    
    private Page<OrderResponseDTO> getOrdersResponseBasedOnRole(MemberEntity member, OrderPageRequestDTO pageRequestDTO) {
        
        Page<OrderResponseDTO> orderResponseDTOPageInfo;
        
        if (member.hasRole(Role.ROLE_ADMIN)) {
            orderResponseDTOPageInfo = orderService.getOrdersWithPaginationAndNoCondition(member, pageRequestDTO);
        } else {
            orderResponseDTOPageInfo = orderService.getOrdersWithPaginationAndNotDeleted(member, pageRequestDTO);
        }
        
        return orderResponseDTOPageInfo;
    }
    
}