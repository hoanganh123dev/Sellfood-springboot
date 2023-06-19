package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> , CrudRepository<Order, Long> {
    @Query("SELECT o FROM Order o join o.customer u WHERE u.username = :email")
    List<Order> findByEmail(@Param("email") String email);

    List<Order> findAllByStatus(int i);
}
