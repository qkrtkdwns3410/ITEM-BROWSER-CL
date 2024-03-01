package com.psj.itembrowser.cart.controller;

import com.psj.itembrowser.cart.domain.dto.request.CartProductDeleteRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductRequestDTO;
import com.psj.itembrowser.cart.domain.dto.request.CartProductUpdateRequestDTO;
import com.psj.itembrowser.cart.domain.dto.response.CartResponseDTO;
import com.psj.itembrowser.cart.service.CartService;
import com.psj.itembrowser.member.annotation.CurrentUser;
import com.psj.itembrowser.member.domain.entity.MemberEntity;
import com.psj.itembrowser.security.common.message.MessageDTO;
import com.psj.itembrowser.security.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;

/**
 * packageName    : com.psj.itembrowser.test.api
 * fileName       : TestApiController
 * author         : ipeac
 * date           : 2023-09-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-09-27        ipeac       최초 생성
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/api/cart")
public class CartApiController {
    private final CartService cartService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable String userId) {
        CartResponseDTO cart = cartService.getCart(userId);

        return ResponseEntity.ok(cart);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
    @PostMapping("")
    public MessageDTO addCart(@Valid @RequestBody CartProductRequestDTO cartProductRequestDTO, @CurrentUser Jwt jwt) {
        log.info("addCart : {}", cartProductRequestDTO);

        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);

        MemberEntity member = MemberEntity.from(customUserDetails.getMemberResponseDTO());

        cartService.addCartProduct(member, cartProductRequestDTO);

        return new MessageDTO(MessageFormat.format(
                "cart add affected : {0} / {1}",
                cartProductRequestDTO.getCartId(),
                cartProductRequestDTO.getProductId()
        ));
    }

    @PutMapping("")
    public MessageDTO modifyCart(@Valid @RequestBody CartProductUpdateRequestDTO cartProductUpdateRequestDTO) {
        cartService.modifyCartProduct(cartProductUpdateRequestDTO);

        return new MessageDTO(MessageFormat.format(
                "cart update affected : {0} / {1}",
                cartProductUpdateRequestDTO.getCartId(),
                cartProductUpdateRequestDTO.getProductId()
        ));
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
    @DeleteMapping("")
    public MessageDTO removeCart(@Valid @RequestBody CartProductDeleteRequestDTO cartProductDeleteRequestDTO, @CurrentUser Jwt jwt) {
        log.info("removeCart : {}", cartProductDeleteRequestDTO);

        UserDetailsServiceImpl.CustomUserDetails customUserDetails = userDetailsService.loadUserByJwt(jwt);

        MemberEntity member = MemberEntity.from(customUserDetails.getMemberResponseDTO());

        cartService.removeCartProduct(cartProductDeleteRequestDTO, member);

        return new MessageDTO(MessageFormat.format(
                "cart delete affected : {0} / {1}",
                cartProductDeleteRequestDTO.getCartId(),
                cartProductDeleteRequestDTO.getProductId()
        ));
    }
}