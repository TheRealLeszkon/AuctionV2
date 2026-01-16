package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.GameDTO;
import com.michael.AuctionV2.domain.entities.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    Game fromDTO(GameDTO gameDTO);
    GameDTO toDTO(Game game);
}
