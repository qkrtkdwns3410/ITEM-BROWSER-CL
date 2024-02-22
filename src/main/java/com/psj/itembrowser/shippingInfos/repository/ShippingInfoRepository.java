package com.psj.itembrowser.shippingInfos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psj.itembrowser.shippingInfos.domain.entity.ShippingInfoEntity;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfoEntity, Long> {
}