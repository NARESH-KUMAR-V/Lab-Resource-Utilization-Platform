package com.labplatform.lab_platform_backend.dto;

import com.labplatform.lab_platform_backend.entity.AvailabilityStatus;
import com.labplatform.lab_platform_backend.entity.Equipment;
import lombok.Data;

@Data
public class EquipmentResponse {

    private Long id;
    private String name;
    private String category;
    private String specifications;
    private String description;
    private String imageUrl;
    private Boolean shared;
    private Long laboratoryId;
    private String laboratoryName;
    private AvailabilityStatus availability;

    public static EquipmentResponse from(Equipment equipment, AvailabilityStatus availability) {

        EquipmentResponse response = new EquipmentResponse();

        response.setId(equipment.getId());
        response.setName(equipment.getName());
        response.setCategory(equipment.getCategory());
        response.setSpecifications(equipment.getSpecifications());
        response.setDescription(equipment.getDescription());
        response.setImageUrl(equipment.getImageUrl());
        response.setShared(equipment.getShared());

        if (equipment.getLaboratory() != null) {
            response.setLaboratoryId(equipment.getLaboratory().getId());
            response.setLaboratoryName(equipment.getLaboratory().getName());
        }

        response.setAvailability(availability);

        return response;
    }
}