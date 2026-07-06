package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "equipment")
@Data
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    @Column(length = 1000)
    private String specifications;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    private String department;

    private String institution;

    @Column(nullable = false)
    private Boolean shared = false;
}