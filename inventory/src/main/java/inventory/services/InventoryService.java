package inventory.services;

import inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean checkIfItemIsInStock(String skuCode) {
        return this.inventoryRepository.existsBySkuCodeAndQuantityGreaterThanEqual(skuCode, 1);
    }
 }
