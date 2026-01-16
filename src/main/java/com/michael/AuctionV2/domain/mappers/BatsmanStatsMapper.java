package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.BatsmanStatsDTO;
import com.michael.AuctionV2.domain.entities.BatsmanStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BatsmanStatsMapper {
    @Mapping(target = "player",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Named("batStatMap")
    BatsmanStats fromDTO(BatsmanStatsDTO batsmanStatsDTO);
    @Named("batStatDTOMap")
    BatsmanStatsDTO toDTO(BatsmanStats batsmanStats);
}
