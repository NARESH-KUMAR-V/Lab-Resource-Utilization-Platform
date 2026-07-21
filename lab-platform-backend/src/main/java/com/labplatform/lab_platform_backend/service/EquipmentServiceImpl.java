package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.Role;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public EquipmentServiceImpl(
            EquipmentRepository equipmentRepository,
            UserRepository userRepository) {

        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Equipment> getAllEquipment(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = user.getRole();

        if (role == Role.SYSTEM_ADMIN) {
            return equipmentRepository.findAll();
        }

        if (role == Role.INSTITUTION_ADMIN) {

            return equipmentRepository.findByLaboratoryInstitutionId(
                    user.getInstitution().getId()
            );

        }

        return equipmentRepository.findByLaboratoryId(
                user.getLaboratory().getId()
        );
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
        equipment.setDescription(updatedEquipment.getDescription());
        equipment.setImageUrl(updatedEquipment.getImageUrl());
        equipment.setStatus(updatedEquipment.getStatus());
        equipment.setShared(updatedEquipment.getShared());
        equipment.setLaboratory(updatedEquipment.getLaboratory());

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

    @Override
    public List<Equipment> getEquipmentByLaboratory(Long laboratoryId) {
        return equipmentRepository.findByLaboratoryId(laboratoryId);
    }
}