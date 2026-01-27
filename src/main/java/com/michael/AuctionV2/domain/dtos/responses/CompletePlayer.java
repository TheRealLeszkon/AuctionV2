package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.dtos.AllRounderStatsDTO;
import com.michael.AuctionV2.domain.dtos.BatsmanStatsDTO;
import com.michael.AuctionV2.domain.dtos.BowlerStatsDTO;
import com.michael.AuctionV2.domain.entities.*;
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
    private PlayerStatus status;

}
