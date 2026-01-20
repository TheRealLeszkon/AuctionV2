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
    @Mapping(source = "batsmanStatsDTO", target = "batsmenStats", qualifiedByName = "batStatMap")
    @Mapping(source = "bowlerStatsDTO",target = "bowlerStats",qualifiedByName = "bowStatMap")
    @Mapping(source = "allRounderStatsDTO",target = "allRounderStats", qualifiedByName = "rounderStatMap")
    Player fromDTO(PlayerDTO playerDTO);

    @Mapping(source = "batsmenStats",target = "batsmanStatsDTO",qualifiedByName = "batStatDTOMap")
    @Mapping(source = "bowlerStats",target = "bowlerStatsDTO",qualifiedByName = "bowStatDTOMap")
    @Mapping(source = "allRounderStats",target = "allRounderStatsDTO", qualifiedByName = "rounderStatDTOMap")
    PlayerDTO toDTO(Player player);
}
