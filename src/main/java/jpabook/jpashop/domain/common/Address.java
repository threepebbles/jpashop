package jpabook.jpashop.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
// JPA 기본 스펙에서 리플렉션, 프록시 같은 기술을 써야하는데 그 때 기본 생성자가 필요함. protected로 선언
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
