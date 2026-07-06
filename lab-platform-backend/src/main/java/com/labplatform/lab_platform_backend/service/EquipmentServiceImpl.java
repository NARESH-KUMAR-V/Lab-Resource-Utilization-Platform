package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @Override
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    @Override
    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment updateEquipment(Long id, Equipment updatedEquipment) {

        Equipment equipment = getEquipmentById(id);

        equipment.setName(updatedEquipment.getName());
        equipment.setCategory(updatedEquipment.getCategory());
        equipment.setSpecifications(updatedEquipment.getSpecifications());
        equipment.setStatus(updatedEquipment.getStatus());
        equipment.setDepartment(updatedEquipment.getDepartment());
        equipment.setInstitution(updatedEquipment.getInstitution());

        return equipmentRepository.save(equipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        Equipment equipment = getEquipmentById(id);
        equipmentRepository.delete(equipment);
    }

    @Override
    public List<Equipment> searchEquipment(String name) {
        return equipmentRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Equipment> getSharedEquipment() {
        return equipmentRepository.findBySharedTrue();
    }

    @Override
    public Equipment shareEquipment(Long id) {

        Equipment equipment = getEquipmentById(id);

        equipment.setShared(true);

        return equipmentRepository.save(equipment);
    }
}