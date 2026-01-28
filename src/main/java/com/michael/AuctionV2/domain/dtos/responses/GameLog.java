package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.enums.PlayerStatus;
import com.michael.AuctionV2.domain.entities.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameLog {
    private Integer playerId;
    private String playerName;
    private IPLAssociation team;
    private TransactionType transactionType;
    private BigDecimal amount;
    private PlayerStatus playerStatus;
    private LocalDateTime time;
}
