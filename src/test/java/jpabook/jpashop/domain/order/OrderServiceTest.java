package jpabook.jpashop.domain.order;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);

        Book book = new Book();
        book.setName("실전 스프링 부트와 JPA 활용");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount = 3;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);
        Assertions.assertThat(findOrder.getStatus()).as("상품 주문시 상태는 ORDER여야 함")
                .isEqualTo(OrderStatus.ORDER);
        Assertions.assertThat(findOrder.getOrderItems().size()).as("주문한 상품 종류 수가 정확해야 함")
                .isEqualTo(1);
        Assertions.assertThat(findOrder.getTotalPrice()).as("주문 가격 = 가격 * 수량")
                .isEqualTo(10000 * orderCount);
        Assertions.assertThat(book.getStockQuantity()).as("주문 수량만큼 재고가 줄어야 함")
                .isEqualTo(10 - orderCount);
    }

    @Test
    public void 주문취소() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given

        //when

        //then
    }
}