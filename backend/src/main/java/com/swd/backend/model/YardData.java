package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YardData {
    private String id;
    private String name;
    private String address;
    private String districtName;
    private String openAt;
    private String closeAt;
    private Integer score;
    private String ownerId;
    private List<String> images;
    private List<SubYardModel> subYards;

    public YardData(YardModel yardModel, List<SubYardModel> subYards) {
        this.ownerId = yardModel.getOwnerId();
        this.id = yardModel.getId();
        this.address = yardModel.getAddress();
        this.closeAt = yardModel.getCloseAt();
        this.name = yardModel.getName();
        this.images = yardModel.getImages();
        this.openAt = yardModel.getOpenAt();
        this.score = yardModel.getScore();
        this.districtName = yardModel.getDistrictName();
        this.subYards = subYards;
    }
}
