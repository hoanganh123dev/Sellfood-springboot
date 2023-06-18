package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdermanagerImpl {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrderHistoryForUser(String email) {
        return orderRepository.findByEmail(email);
    }
}
