package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.StudentDTO;

public interface StudentServiceClient {

    StudentDTO findByCnie(String cnie);
}
