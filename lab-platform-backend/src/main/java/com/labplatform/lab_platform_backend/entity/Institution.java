package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "institutions")
@Data
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private String email;

    private String phone;
}