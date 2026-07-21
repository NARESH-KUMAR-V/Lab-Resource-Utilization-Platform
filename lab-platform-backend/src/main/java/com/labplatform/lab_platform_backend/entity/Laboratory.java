package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "laboratories")
@Data
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String department;

    private String location;

    private String hodName;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
}