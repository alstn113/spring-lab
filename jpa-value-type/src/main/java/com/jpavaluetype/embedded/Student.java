package com.jpavaluetype.embedded;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 기본값 타입

    @Embedded
    // attribute override 는 DB Column 이름을 재정의할 수 있다.
    @AttributeOverrides(value = {
            @AttributeOverride(name = "street", column = @Column(name = "home_street")),
            @AttributeOverride(name = "city", column = @Column(name = "home_city"))
    })
    private Address homeAddress; // 임베디드 타입

    @Embedded
    private Age age;

    public Student(String name, Address homeAddress, Age age) {
        this(null, name, homeAddress, age);
    }

    public Student(Long id, String name, Address homeAddress, Age age) {
        this.id = id;
        this.name = name;
        this.homeAddress = homeAddress;
        this.age = age;
    }

    public String getCity() {
        return homeAddress.getCity();
    }

    public String getStreet() {
        return homeAddress.getStreet();
    }

    public int getAge() {
        return age.getValue();
    }
}
