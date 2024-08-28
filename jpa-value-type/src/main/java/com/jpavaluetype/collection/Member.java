package com.jpavaluetype.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * @ElementCollection: 값 타입 컬렉션을 매핑할 때 사용
     * @CollectionTable: 값 타입 컬렉션을 저장할 테이블 지정
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "favorite_food", joinColumns = @JoinColumn(name = "member_id")) //
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "address", joinColumns = @JoinColumn(name = "member_id"))
    private List<Address> addressHistory = new ArrayList<>();

    public Member(String name, Set<String> favoriteFoods, List<Address> addressHistory) {
        this(null, name, favoriteFoods, addressHistory);
    }

    public void addFavoriteFood(String food) {
        favoriteFoods.add(food);
    }

    public void addAddressHistory(Address address) {
        addressHistory.add(address);
    }
}
