package com.jpavaluetype.embedded;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmbeddedTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Student 저장 테스트")
    void saveMember() {
        // given
        Address address = new Address("서울", "강가");
        Age age = new Age(20);
        Student student = new Student("홍길동", address, age);

        // when
        studentRepository.save(student);

        // then
        Student found = studentRepository.findByHomeAddress_City("서울").get();
        assertThat(found.getCity()).isEqualTo("서울");
    }

}
