package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.TeamDTO;
import com.michael.AuctionV2.domain.entities.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamDTO toDTO(Team team);
    Team fromDTO(TeamDTO teamDTO);
}
