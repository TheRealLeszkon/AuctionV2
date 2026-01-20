package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.IPLAssociation;
import com.michael.AuctionV2.domain.entities.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseConfirmation {
    private PlayerStatus playerStatus;
    private IPLAssociation soldTo;
    private BigDecimal soldFor;
}
