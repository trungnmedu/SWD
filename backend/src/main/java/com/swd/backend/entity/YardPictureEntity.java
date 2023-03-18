package com.swd.backend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "yard_picture")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardPictureEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "ref_id")
    private String refId;
    @Column(name = "image")
    private String image;
}
