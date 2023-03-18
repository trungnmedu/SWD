package com.swd.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Table(name = "yards")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "create_at")
    private Timestamp createAt;
    @Column(name = "address")
    private String address;
    @Column(name = "district_id")
    private int districtId;
    @Column(name = "is_active")
    private boolean active;
    @Column(name = "is_deleted")
    private boolean deleted;
    @Column(name = "owner_id")
    private String ownerId;
    @Column(name = "open_at")
    private LocalTime openAt;
    @Column(name = "close_at")
    private LocalTime closeAt;
    @Column(name = "slot_duration")
    private int slotDuration;
    @Column(name = "score")
    private int score;
    @Column(name = "number_of_vote")
    private int numberOfVote;
    @Column(name = "reference", insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reference;
    @Column(name = "number_of_bookings", insertable = false)
    private Integer numberOfBookings;
}
