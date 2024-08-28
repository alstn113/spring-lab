package com.jpavaluetype.collection;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CollectionTest {

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    /**
     * CollectionTable에서
     * Set인 경우 추가할 때 insert 한 번
     * List인 경우 추가할 때 모두 delete 후 insert
     */
    @Test
    @DisplayName("CollectionTable 테스트")
    void saveMember() {
        Set<String> favoriteFoods = new HashSet<>(Set.of("치킨", "피자"));
        List<Address> addresses = new ArrayList<>(List.of(
                new Address("서울", "강남"),
                new Address("서울", "강북")
        ));
        Member member = new Member("홍길동", favoriteFoods, addresses);
        Member saved1 = memberRepository.save(member);

        System.out.println("-- 멤버가 저장되지 않아서 favorite food 반영되지 않음 --");

        saved1.addFavoriteFood("족발"); // 아직 안들어감.
        assertThat(memberRepository.findById(saved1.getId()).get().getFavoriteFoods())
                .contains("치킨", "피자");

        System.out.println("-- 멤버 저장 후 favorite food 반영됨 --");

        Member saved2 = memberRepository.save(saved1);// member 저장 시 반영됨. insert 한 번
        assertThat(memberRepository.findById(saved2.getId()).get().getFavoriteFoods())
                .contains("치킨", "피자", "족발");

        System.out.println("-- 멤버 저장되지 않아서 address 반영되지 않음 --");
        Address newAddress = new Address("부산", "해운대");
        saved2.addAddressHistory(newAddress); // 아직 안들어감.
        assertThat(memberRepository.findById(saved2.getId()).get().getAddressHistory())
                .containsExactly(
                        new Address("서울", "강남"),
                        new Address("서울", "강북")
                );

        System.out.println("-- 멤버 저장 후 address 반영됨 --");
        Member saved3 = memberRepository.save(saved2);// member 저장 시 반영됨. 모두 delete 후 3개를 insert
        assertThat(memberRepository.findById(saved3.getId()).get().getAddressHistory())
                .containsExactly(
                        new Address("서울", "강남"),
                        new Address("서울", "강북"),
                        new Address("부산", "해운대")
                );

        System.out.println("---------------------------");
    }
}
