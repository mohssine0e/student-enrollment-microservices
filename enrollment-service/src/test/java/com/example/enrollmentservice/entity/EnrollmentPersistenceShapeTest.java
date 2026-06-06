package com.example.enrollmentservice.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class EnrollmentPersistenceShapeTest {

    @Test
    void enrollmentEntityStoresOnlyEnrollmentAndForeignKeyData() {
        Set<String> fields = Arrays.stream(Enrollment.class.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(Field::getName)
                .collect(Collectors.toSet());

        assertThat(fields).containsExactlyInAnyOrder("id", "studentId", "courseId", "enrolledAt");
    }
}
