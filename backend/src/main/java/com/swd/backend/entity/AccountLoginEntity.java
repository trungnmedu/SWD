package com.swd.backend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "account_login")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "account_id")
    private String userId;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "is_logout")
    private boolean isLogout = false;
}
