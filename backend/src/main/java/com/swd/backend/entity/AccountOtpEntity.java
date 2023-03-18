package com.swd.backend.entity;

import com.swd.backend.utils.DateHelper;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_otp")
public class AccountOtpEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "account_id")
    private String userId;
    @Column(name = "otp_code")
    private String otpCode;
    @Column(name = "expire_at")
    private Timestamp expireAt;
    @Column(name = "create_at")
    @Builder.Default
    private Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
    @Column(name = "used")
    @Builder.Default
    private boolean isUsed = false;

}
