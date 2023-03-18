package com.swd.backend.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "type")
    private String type;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
    @Column(name = "voucher_code")
    private String voucherCode;
    @Column(name = "max_quantity")
    private int maxQuantity;
    @Column(name = "usages")
    private int usages;
    @Column(name = "discount")
    private float discount;
    @Column(name = "start_date")
    private Timestamp startDate;
    @Column(name = "end_date")
    private Timestamp endDate;
    @Column(name = "created_by")
    private String createdByAccountId;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "status")
    private String status;
    @Column(name = "reference", insertable = false, updatable = true)
    private int reference;
}
