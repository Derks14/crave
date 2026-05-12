package order.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryService {

    @GetExchange("/api/inventory")
    @CircuitBreaker(name="inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    boolean isSkuCodeItemInStock(@RequestParam String skuCode);

    default boolean fallbackMethod(String skuCode, Throwable throwable) {
        System.out.println("Cannot get inventory for skuCode " + skuCode);
        return false;
    }

}
