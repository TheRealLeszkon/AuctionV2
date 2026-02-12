package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.GameDTO;
import com.michael.AuctionV2.domain.entities.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {
    Game fromDTO(GameDTO gameDTO);
    @Mapping(target = "teamPasswords", ignore = true)
    GameDTO toDTO(Game game);
}
