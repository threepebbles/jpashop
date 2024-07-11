package jpabook.jpashop.domain.delivery;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jpabook.jpashop.domain.common.Address;
import jpabook.jpashop.domain.order.Order;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)    // default가 ORDINAL. 중간에 새로운 status가 추가되면 큰일남.. 무조건 EnumType.STRING 사용.
    private DeliveryStatus status;
}
