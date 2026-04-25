package inventory.controllers;

import inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode) {
        return this.service.checkIfItemIsInStock(skuCode);
    }
}
