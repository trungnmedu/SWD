package com.swd.backend.entity;

import com.swd.backend.utils.DateHelper;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "votes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VoteEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "booking_id")
    private String bookingId;
    @Column(name = "score")
    private int score;
    @Column(name = "comment")
    private String comment;
    @Column(name = "date")
    @Builder.Default
    private Timestamp date = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
    @Column(name = "is_deleted")
    @Builder.Default
    private boolean deleted = false;
}
