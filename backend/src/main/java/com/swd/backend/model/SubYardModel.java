package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class SubYardModel {
    private String id;
    private String name;
    private String parentYard;
    private String typeYard;
    private Timestamp createAt;
    private Integer reference;
    private boolean isActive;
}
