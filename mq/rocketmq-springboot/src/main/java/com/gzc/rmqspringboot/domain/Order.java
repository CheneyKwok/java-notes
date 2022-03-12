package com.gzc.rmqspringboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private Long orderId;

    private String desc;
}
