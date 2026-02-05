package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDTO {
    private Integer id;
    private Integer gameId;
    private String name;
    private IPLAssociation association;
    private BigDecimal balance;
    private Integer points;

    //player counts
    private Integer playerCount;
    private Integer allRounderCount;
    private Integer batsmanCount;
    private Integer bowlerCount;
    private Integer wicketKeeperCount;
    private Integer uncappedCount;
    private Integer legendCount;
    private Integer specialCount;
    private Integer foreignCount;
}
