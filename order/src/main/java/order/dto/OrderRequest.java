package order.dto;

import java.math.BigDecimal;

public record OrderRequest(String skuCode, BigDecimal price, Integer quantity, UserDetails userDetails ) {

    public record UserDetails(String email, String firstname, String lastname) {

    }
}

