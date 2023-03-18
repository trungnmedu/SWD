package com.swd.backend.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
public class BookingEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "slot_id")
    private int slotId;
    @Column(name = "status")
    private String status;
    @Column(name = "date")
    private Timestamp date;
    @Column(name = "note")
    private String note;
    @Column(name = "price")
    private int price;
    @Column(name = "book_at")
    private Timestamp bookAt;
    @Column(name = "reference", insertable = false)
    private long reference;
    @Column(name = "sub_yard_id")
    private String subYardId;
    @Column(name = "big_yard_id")
    private String bigYardId;
    @Column(name = "original_price")
    private Integer originalPrice;
    @Column(name = "voucher_code")
    private String voucherCode;
}