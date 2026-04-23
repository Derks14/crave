package food.controllers;

import food.dto.FoodRequest;
import food.dto.FoodResponse;
import food.model.Food;
import food.services.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse create(@RequestBody FoodRequest foodRequest) {
        return foodService.createFood(foodRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FoodResponse> getAllFoods() {
        return foodService.getAllFoods();
    }
}
