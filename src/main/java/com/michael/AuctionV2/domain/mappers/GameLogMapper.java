package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.responses.GameLog;
import com.michael.AuctionV2.domain.entities.GameTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Repository;

@Mapper(componentModel = "spring")
public interface GameLogMapper{

    @Mapping(source = "player.id", target ="playerId")
    @Mapping(source = "player.name", target = "playerName")
    @Mapping(source = "team.association",target = "team")
    @Mapping(source = "type",target = "transactionType")
    GameLog toDTO(GameTransaction gameTransaction);
}
