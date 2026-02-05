package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.dtos.TeamDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ranking { //TODO name as rank
    private Integer place;
    private TeamDTO teamStats;
    private List<String> purchases;
    private Boolean isQualified;
}
