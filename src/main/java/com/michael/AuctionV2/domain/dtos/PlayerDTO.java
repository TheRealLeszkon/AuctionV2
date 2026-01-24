package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private Integer id;
    private String name;
    private String imageLink;
    private PlayerType type;
    private Boolean isForeign;
    private Boolean isUncapped;
    private Boolean isLegend;
    private String country;
    private BatsmanStatsDTO batsmanStats;
    private BowlerStatsDTO bowlerStats;
    private AllRounderStatsDTO allRounderStats;
}
