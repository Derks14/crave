package food.controllers;

import food.dto.FoodRequest;
import food.dto.FoodResponse;
import food.model.Food;
import food.services.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    AtomicInteger counter = new AtomicInteger();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse create(@RequestBody FoodRequest foodRequest) {
        return foodService.createFood(foodRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FoodResponse> getAllFoods() throws InterruptedException {
        int current = counter.incrementAndGet();

        if (current % 2 == 0) Thread.sleep(50000);
        return foodService.getAllFoods();
    }
}
