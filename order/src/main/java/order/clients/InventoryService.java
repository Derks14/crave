package order.clients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

//@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryService {

//    @GetMapping(value = "/api/inventory")
    @GetExchange("/api/inventory")
    boolean isSkuCodeItemInStock(@RequestParam String skuCode);

}
