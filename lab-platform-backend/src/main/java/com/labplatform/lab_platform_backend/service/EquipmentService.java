package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;

import java.util.List;

public interface EquipmentService {

    List<Equipment> getAllEquipment(String email);

    Equipment getEquipmentById(Long id);

    Equipment createEquipment(Equipment equipment);

    Equipment updateEquipment(Long id, Equipment equipment);

    void deleteEquipment(Long id);

    List<Equipment> searchEquipment(String name);

    List<Equipment> getSharedEquipment();

    Equipment shareEquipment(Long id);

    List<Equipment> getEquipmentByLaboratory(Long laboratoryId);
}