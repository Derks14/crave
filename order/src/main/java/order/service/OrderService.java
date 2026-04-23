package order.service;

import lombok.RequiredArgsConstructor;
import order.dto.OrderRequest;
import order.models.Order;
import order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order placeOrder(OrderRequest request) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setSkuCode(request.skuCode());
        order.setPrice(request.price());
        order.setQuantity(request.quantity());

        return orderRepository.save(order);
    }
}
