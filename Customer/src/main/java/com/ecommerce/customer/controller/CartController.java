package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class CartController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        if (customer == null) {
            return "redirect:/login";
        }

        ShoppingCart shoppingCart = customer.getShoppingCart();

        if (shoppingCart == null) {
            model.addAttribute("check", "No item in your cart");
            model.addAttribute("subTotal", 0); // Sửa lại giá trị subTotal thành 0 nếu không có sản phẩm trong giỏ hàng
            model.addAttribute("shoppingCart", new ShoppingCart()); // Tạo một giỏ hàng rỗng để hiển thị trên view
        } else {
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("subTotal", shoppingCart.getTotalPrices());
            model.addAttribute("shoppingCart", shoppingCart);
        }

        return "cart";
    }



    @PostMapping("/add-to-cart")
    public String addItemToCart(
            @RequestParam("id") Long productId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(productId);

        if (product == null) {
            // Xử lý khi không tìm thấy sản phẩm với ID tương ứng
            return "redirect:/error"; // Chuyển hướng đến trang lỗi
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        if (customer == null) {
            return "redirect:/login";
        }

        ShoppingCart cart = cartService.addItemToCart(product, quantity, customer);

        if (cart == null) {
            // Xử lý khi không thể thêm sản phẩm vào giỏ hàng
            return "redirect:/error"; // Chuyển hướng đến trang lỗi
        }

        return "redirect:" + request.getHeader("Referer");
    }



    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=update")
    public String updateCart(@RequestParam("quantity") int quantity,
                             @RequestParam("id") Long productId,
                             Model model,
                             Principal principal
                             ){

        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            Customer customer = customerService.findByUsername(username);
            Product product = productService.getProductById(productId);
            ShoppingCart cart = cartService.updateItemInCart(product, quantity, customer);

            model.addAttribute("shoppingCart", cart);
            return "redirect:/cart";
        }

    }


    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=delete")
    public String deleteItemFromCart(@RequestParam("id") Long productId,
                                     Model model,
                                     Principal principal){
        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            Customer customer = customerService.findByUsername(username);
            Product product = productService.getProductById(productId);
            ShoppingCart cart = cartService.deleteItemFromCart(product, customer);
            model.addAttribute("shoppingCart", cart);
            return "redirect:/cart";
        }

    }



}
