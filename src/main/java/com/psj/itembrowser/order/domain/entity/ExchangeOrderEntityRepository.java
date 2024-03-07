package com.psj.itembrowser.order.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeOrderEntityRepository extends JpaRepository<ExchangeOrderEntity, Long> {
}