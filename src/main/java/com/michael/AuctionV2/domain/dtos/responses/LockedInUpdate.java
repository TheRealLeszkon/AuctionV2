package com.michael.AuctionV2.domain.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockedInUpdate {
    private int lockedInCount;
    private List<String> lockedInTeams;
}
