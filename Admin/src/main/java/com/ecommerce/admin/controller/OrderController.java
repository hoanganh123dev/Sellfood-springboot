package com.ecommerce.admin.controller;

import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.impl.OrdermanagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @GetMapping("/Order")
    public String Order(Model model, Principal principal){
        if(principal == null){
            return "redirect:/login";
        }
        List<Order> orders = orderService.findAll();
        model.addAttribute("categories", orders);
        model.addAttribute("size", orders.size());
        model.addAttribute("title", "Order");
        model.addAttribute("products", orderRepository.findAll());
        return "Order";
    }
    @GetMapping("/UpdateStatus")
    public String update(Model model, @RequestParam("type") Integer type, @RequestParam("id") Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (type == 1) {
            assert order != null;
            order.setStatus(1);
            orderRepository.save(order);
        }
        if (type == 2) {
            assert order != null;
            order.setStatus(2);
            orderRepository.save(order);
        }


        return "redirect:Order";
    }



}
