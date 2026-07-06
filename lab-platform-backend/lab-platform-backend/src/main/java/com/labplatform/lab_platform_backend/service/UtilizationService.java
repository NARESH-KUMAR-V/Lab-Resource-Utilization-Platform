package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.Utilization;

import java.util.List;

public interface UtilizationService {

    List<Utilization> getAllUtilizations();

    Utilization getUtilizationById(Long id);

    Utilization createUtilization(Utilization utilization);

    Utilization updateUtilization(Long id, Utilization utilization);

    void deleteUtilization(Long id);

    Utilization createFromBooking(Booking booking);

    void completeUtilization(Booking booking);
}