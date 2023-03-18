package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteModel {
    private String accountId;
    private String accountFullName;
    private String accountAvatar;
    private String bookingId;
    private String voteId;
    private String postedAt;
    private String yardName;
    private String subYardName;
    private String typeName;
    private String address;
    private String startTime;
    private String endTime;
    private String date;
    private Integer score;
    private String comment;
}
