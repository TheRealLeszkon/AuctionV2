package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.AllRounderStats;
import com.michael.AuctionV2.domain.entities.BatsmanStats;
import com.michael.AuctionV2.domain.entities.BowlerStats;
import com.michael.AuctionV2.domain.entities.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletePlayer {
    private Integer id;
    private String name;
    private String imageLink;
    private PlayerType type;
    private Boolean isUncapped;
    private Boolean isLegend;
    private String country;
    private BatsmanStatsDTO batsmanStats;
    private BowlerStatsDTO bowlerStats;
    private AllRounderStatsDTO allRounderStats;
    private Integer setId;
    private BigDecimal price;
    private Integer points;
    private Integer order;

}
