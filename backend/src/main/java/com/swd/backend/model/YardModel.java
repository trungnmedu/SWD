package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardModel {
    private String id;
    private String name;
    private String address;
    private String districtName;
    private String province;
    private String openAt;
    private String closeAt;
    private int reference;
    private boolean isActive;
    private Timestamp createdAt;
    private int score;
    private String ownerId;
    private List<String> images;
}
