package com.swd.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "provinces")
@Getter
@Setter
public class ProvinceEntity {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "province_name")
    private String provinceName;

}
