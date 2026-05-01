package order.clients;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryClient {

    @GetMapping(value = "/api/inventory")
    boolean isSkuCodeItemInStock(@RequestParam String skuCode);
}
