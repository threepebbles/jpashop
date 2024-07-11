package jpabook.jpashop.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jpabook.jpashop.domain.order.Order;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
// 이론적으로는 Getter, Setter 모두 제공하지 않고, 필요한 메서드만 제공하는 것이 이상적. 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로 Getter의 경우 모두 열어두는 것이 편리함. Getter는 아무리 호출해도 호출하는 것 만으로는 어떤 일이 발생하지 않음. 하지만 Setter는 호출하면 데이터가 변함. 그래서 엔티티를 변경할 때는 Setter 대신 변경지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 함.
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
