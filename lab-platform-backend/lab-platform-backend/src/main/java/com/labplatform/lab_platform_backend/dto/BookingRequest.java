package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    private Long equipmentId;

    private LocalDate bookingDate;

    private String purpose;
}