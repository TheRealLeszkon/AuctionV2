package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.AllRounderStatsDTO;
import com.michael.AuctionV2.domain.entities.AllRounderStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AllRounderStatsMapper {
    @Mapping(target = "player",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Named("rounderStatMap")
    AllRounderStats fromDTO(AllRounderStatsDTO allRounderStatsDTO);
    @Named("rounderStatDTOMap")
    AllRounderStatsDTO toDTO(AllRounderStats allRounderStats);
}
