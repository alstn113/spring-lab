package com.jpavaluetype.embedded;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 임베디드 타입 기본 생성자 필요!
@Getter
class Age {

    @Column(name = "age", nullable = false)
    private int value;

    public Age(int value) {
        this.value = value;
    }


    // 값 타입은 값이 같으면 동일한 것으로 간주한다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Age age = (Age) o;
        return value == age.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
