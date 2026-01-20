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
    Integer id;
    String name;
    String imageLink;
    PlayerType type;
    BatsmanStatsDTO batsmanStats;
    BowlerStatsDTO bowlerStats;
    AllRounderStatsDTO allRounderStats;
    Integer setId;
    BigDecimal price;
    Integer points;
    Integer order;

}
