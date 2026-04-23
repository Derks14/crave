package food.services;


import food.dto.FoodRequest;
import food.dto.FoodResponse;
import food.model.Food;
import food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodResponse createFood(FoodRequest foodRequest) {
        Food food = Food.builder()
                .name(foodRequest.name())
                .description(foodRequest.description())
                .price(foodRequest.price())
                .build();

        food = foodRepository.save(food);
        log.info("Food {} is saved", food.getId());
        return new FoodResponse(food.getId(), food.getName(), food.getDescription(), food.getPrice().toEngineeringString());
    }

    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream().map(food -> new FoodResponse(
                food.getId(),
                        food.getName(),
                        food.getDescription(),
                        food.getPrice().toEngineeringString()  ))
                .toList();
    }


}
