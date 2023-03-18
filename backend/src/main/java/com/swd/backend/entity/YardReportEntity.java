package com.swd.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "yard_report")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class YardReportEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "yard_id")
    private String yardId;
    @Column(name = "reason")
    private String reason;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "status")
    private String status;
    @Column(name = "reference", insertable = false)
    private int reference;
}
