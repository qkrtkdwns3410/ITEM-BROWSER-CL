package com.psj.itembrowser.security.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.psj.itembrowser.member.annotation.CurrentUser;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.order.domain.entity.OrderEntity;

/**
 * packageName    : com.psj.itembrowser.order.service.impl fileName       : AuthenticationService author         : ipeac date           : 2024-02-05 description    : =========================================================== DATE              AUTHOR NOTE ----------------------------------------------------------- 2024-02-05        ipeac       최초 생성
 */
@Service
public interface AuthenticationService {

    void authorizeOrdersWhenCustomer(Page<OrderEntity> orders, @CurrentUser MemberEntity member);
}