package com.example.mockbit.order.domain.repository;

import com.example.mockbit.order.domain.OrderResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderResultRepository extends JpaRepository<OrderResult, Long> {
}
