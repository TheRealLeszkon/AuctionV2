package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.PlayerDTO;
import com.michael.AuctionV2.domain.entities.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {BatsmanStatsMapper.class,AllRounderStatsMapper.class,BowlerStatsMapper.class}
)
public interface PlayerMapper {
    @Mapping(source = "batsmanStats", target = "batsmenStats", qualifiedByName = "batStatMap")
    @Mapping(source = "bowlerStats",target = "bowlerStats",qualifiedByName = "bowStatMap")
    @Mapping(source = "allRounderStats",target = "allRounderStats", qualifiedByName = "rounderStatMap")
    Player fromDTO(PlayerDTO playerDTO);

    @Mapping(source = "batsmenStats",target = "batsmanStats",qualifiedByName = "batStatDTOMap")
    @Mapping(source = "bowlerStats",target = "bowlerStats",qualifiedByName = "bowStatDTOMap")
    @Mapping(source = "allRounderStats",target = "allRounderStats", qualifiedByName = "rounderStatDTOMap")
    PlayerDTO toDTO(Player player);
}
