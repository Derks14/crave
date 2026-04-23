package food.dto;

import java.math.BigDecimal;

public record FoodRequest (String name, String description, BigDecimal price) { }
