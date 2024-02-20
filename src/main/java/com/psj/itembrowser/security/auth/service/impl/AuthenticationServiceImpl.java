package com.psj.itembrowser.security.auth.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.member.domain.vo.Role;
import com.psj.itembrowser.order.domain.entity.OrderEntity;
import com.psj.itembrowser.security.auth.service.AuthenticationService;
import com.psj.itembrowser.security.common.exception.BadRequestException;
import com.psj.itembrowser.security.common.exception.ErrorCode;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.psj.itembrowser.order.service.impl fileName       : AuthenticationService author         : ipeac date           : 2024-02-05 description    : =========================================================== DATE              AUTHOR NOTE ----------------------------------------------------------- 2024-02-05        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void authorizeOrdersWhenCustomer(final Page<OrderEntity> orders, MemberEntity currentMember) {
        log.info("AuthenticationServiceImpl#authorizeOrders started");

        if (orders.isEmpty()) {
            log.info("authorizeOrders orders is empty");

            return;
        }

        if (currentMember.hasRole(Role.ROLE_CUSTOMER)) {
            log.info("authorizeOrders currentRole : {}", currentMember.getRole());

            orders.forEach(order -> {
                if (!currentMember.isSame(order.getMember())) {
                    throw new BadRequestException(ErrorCode.ORDER_IS_NOT_MATCH_CURRENT_MEMBER);
                }

                log.info("authorizeOrders currentMember is same with order member ==> passed");
            });

            return;
        }

        throw new BadRequestException(ErrorCode.INVALID_MEMBER_ROLE);
    }
}