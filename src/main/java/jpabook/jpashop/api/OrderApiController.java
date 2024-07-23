package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderItem;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // iter치면 intelliJ 예약어 사용 가능
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * 엔티티를 직접 노출하는 문제 해결
     * <p>
     * 하지만, 쿼리가 많이 발생하므로 최적화가 필요함
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(OrderDto::new)
                .toList();
    }

    /**
     * 컬렉션 페치 조인 시 주의할 점
     * <p>
     * 1. 컬렉션 페치 조인은 1개만 사용할 수 있음. 컬렉션 둘 이상에 페치 조인 사용하면 데이터가 부정합하게 조회될 수 있음.
     * <p>
     * 2. 컬렉션 페치 조인에 대해 페이징을 사용하면 안됨. 일대다 에서 일(1)을 기준으로 페이징하고 싶은데, 다(N)를 기준으로 페이징이 일어남.
     * <p>
     * 데이터를 DB에서 읽어오고 메모리(distinct 처리가 되지 않은 상태)에서 페이징하기 때문. 하이버네이트는 경고 로그를 남김. 최악에 시스템에 장애가 생길 수 있음
     * <p>
     * WARN: firstResult/maxResults specified with collection fetch; applying in memory
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto::new)
                .toList();
    }


    //==DTO==//
    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // OrderItem도 엔티티이므로 외부에 노출되면 안됨
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // 프록시 객체 초기화
            orderItems = order.getOrderItems().stream().map(OrderItemDto::new)
                    .toList();
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;    // 상품명
        private int orderPrice; // 주문 가격
        private int count;  // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
