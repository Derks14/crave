package order.service;

import events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import order.clients.InventoryService;
import order.dto.OrderRequest;
import order.models.Order;
import order.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public Order placeOrder(OrderRequest request) {

        boolean isFoodAvailable = inventoryService.isSkuCodeItemInStock(request.skuCode());

        if (isFoodAvailable) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setSkuCode(request.skuCode());
            order.setPrice(request.price());
            order.setQuantity(request.quantity());

            OrderPlacedEvent placedOrder = new OrderPlacedEvent();
            placedOrder.setEmail(request.userDetails().email());
            placedOrder.setOrderNumber(order.getOrderNumber());
            placedOrder.setFirstname(request.userDetails().firstname());
            placedOrder.setLastname(request.userDetails().lastname());

            log.info("about to create an order placed event in kafka");

            kafkaTemplate.send("placed-orders", order.getOrderNumber(),  placedOrder);

            log.info("order event placed successfully, currently processing in kafka");

            return orderRepository.save(order);
        }

        throw new RuntimeException("the food %s not available".formatted(request.skuCode()));


    }
}
