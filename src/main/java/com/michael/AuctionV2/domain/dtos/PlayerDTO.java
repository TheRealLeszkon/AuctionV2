package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.PlayerType;
import lombok.Data;

@Data
public class PlayerDTO {
    private Integer id;
    private String name;
    private String imageLink;
    private PlayerType type;
    private Boolean isForeign;
    private BatsmanStatsDTO batsmanStats;
    private BowlerStatsDTO bowlerStats;
    private AllRounderStatsDTO allRounderStats;
}
