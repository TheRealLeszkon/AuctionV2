package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.TeamDTO;
import com.michael.AuctionV2.domain.entities.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamDTO toDTO(Team team);
    @Mapping(target = "isQualified", ignore = true)
    @Mapping(target = "selectionLocked", ignore = true)
    Team fromDTO(TeamDTO teamDTO);
}
