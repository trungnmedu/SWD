package com.swd.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "districts")
@Getter
@Setter
public class DistrictEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "province_id")
    private int provinceId;
    @Column(name = "district_name")
    private String districtName;
}
