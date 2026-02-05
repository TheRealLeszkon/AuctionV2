package com.michael.AuctionV2.domain.dtos.requests;

import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstituteRemovalRequest {
    private IPLAssociation teamAssociation;
    private List<Integer> substitutes;
}
