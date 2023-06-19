package com.ecommerce.admin.controller;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class AdminController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @GetMapping("/home")
    public String index(Model model) throws JsonProcessingException, ParseException {
        double sum = orderRepository.findAll().stream().mapToDouble(Order::getTotalPrice).sum();

        // Create a custom Vietnamese locale
        Locale vietnameseLocale = new Locale("vi", "VN");

        // Create a custom currency instance for VND
        Currency vndCurrency = Currency.getInstance("VND");

        // Create a decimal format pattern for Vietnamese locale with VND currency
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(vietnameseLocale);
        symbols.setCurrencySymbol("₫"); // Set the currency symbol to VND symbol
        DecimalFormat vndCurrencyFormat = new DecimalFormat("#,###", symbols);
        vndCurrencyFormat.setCurrency(vndCurrency);

        // Format the amount as VND currency
        String formattedAmount = vndCurrencyFormat.format(sum);
        int ordersNew = orderRepository.findAllByStatus(0).size();
        int customers = customerRepository.findAll().size();
        int ordersReject = orderRepository.findAllByStatus(2).size();
        int maxDate = 30;
        LocalDate today = LocalDate.now();
        String lastMonthDate, lastMonth, thisMonth;
        int getDayLastMonth, startDayLastMonth, maxDayLastMonth;

        if (maxDate > today.getDayOfMonth()) {
            getDayLastMonth = 30 - today.getDayOfMonth();
            lastMonthDate = today.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            lastMonth = today.minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
            thisMonth = today.format(DateTimeFormatter.ofPattern("MM"));
            maxDayLastMonth = today.minusMonths(1).lengthOfMonth();
            startDayLastMonth = maxDayLastMonth - getDayLastMonth;
        } else {
            getDayLastMonth = 31 - today.getDayOfMonth();
            lastMonthDate = today.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            lastMonth = today.minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
            thisMonth = today.format(DateTimeFormatter.ofPattern("MM"));
            maxDayLastMonth = today.minusMonths(1).lengthOfMonth();
            startDayLastMonth = maxDayLastMonth - getDayLastMonth;
        }

        Map<String,Float> map = new LinkedHashMap<>();
        for (int i = startDayLastMonth; i <= maxDayLastMonth; i++) {

            String key = i + "-" +  lastMonth;
            map.put(key, 0f);
        }

        for (int i = 1; i <= today.getDayOfMonth(); i++) {
            String key = i + "-" + thisMonth;
            map.put(key, 0f);
        }
        System.out.println(map);
        List<Map<String, Object>> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("d-MM")),
                        Collectors.summingDouble(Order::getTotalPrice)
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("TimeDate", entry.getKey());
                    tempMap.put("Sum", entry.getValue().floatValue());
                    return tempMap;
                })
                .collect(Collectors.toList());

        for (Map<String, Object> each : result) {
            String timeDate = (String) each.get("TimeDate");
            System.out.println(each.get("Sum"));
            System.out.println(timeDate);
            System.out.println(map.get("13-06"));

            if (map.containsKey(timeDate)) {

                map.put(timeDate, (Float) each.get("Sum"));
            }
        }

        System.out.println(new ObjectMapper().writeValueAsString(map.keySet().toArray()));
        System.out.println(new ObjectMapper().writeValueAsString(map.values().toArray()));
        model.addAttribute("sum", formattedAmount);
        model.addAttribute("Arr1", new ObjectMapper().writeValueAsString(map.keySet().toArray()));
        model.addAttribute("Arr2", new ObjectMapper().writeValueAsString(map.values().toArray()));
        model.addAttribute("orders_new", ordersNew);
        model.addAttribute("cus", customers);
        model.addAttribute("orders_reject", ordersReject);
        return "index";
    }
}
