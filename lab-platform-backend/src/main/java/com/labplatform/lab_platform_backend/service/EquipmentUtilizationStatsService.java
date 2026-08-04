package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.EquipmentUtilizationStatsDTO;
import com.labplatform.lab_platform_backend.dto.UtilizationDashboardDTO;
import com.labplatform.lab_platform_backend.entity.EquipmentUtilizationStats;

import java.util.List;

public interface EquipmentUtilizationStatsService {

    void computeAndSaveAllStats();

    void computeAndSaveStatsForEquipment(Long equipmentId);

    EquipmentUtilizationStats getStatsByEquipmentId(Long equipmentId);

    List<EquipmentUtilizationStatsDTO> getAllStats();

    List<EquipmentUtilizationStatsDTO> getStatsByRanking();

    UtilizationDashboardDTO getUtilizationDashboard();
}
