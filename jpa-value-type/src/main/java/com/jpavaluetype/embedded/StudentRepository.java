package com.jpavaluetype.embedded;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<Student, Long> {

    // 임베디드 타입을 사용하기 위해서는 "임베디드_필드"로 접근해야 한다.
    // getCity 메서드를 정의했다고 City 가 자동완성되지만 안된다. 주의!
    Optional<Student> findByHomeAddress_City(String city);
}
