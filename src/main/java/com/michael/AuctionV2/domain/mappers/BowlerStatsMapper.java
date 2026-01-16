package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.BowlerStatsDTO;
import com.michael.AuctionV2.domain.entities.BowlerStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BowlerStatsMapper {
    @Mapping(target = "player",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Named("bowStatMap")
    BowlerStats fromDTO(BowlerStatsDTO bowlerStatsDTO);
    @Named("bowStatDTOMap")
    BowlerStatsDTO toDTO(BowlerStats bowlerStats);
}
