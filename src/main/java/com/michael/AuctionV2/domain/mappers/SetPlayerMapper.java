package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.SetPlayerDTO;
import com.michael.AuctionV2.domain.entities.SetPlayer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface SetPlayerMapper {

    @Mapping(target = "id", expression = "java(new SetPlayerId(dto.getSetId(), dto.getPlayerId()))")
    SetPlayer fromDTO(SetPlayerDTO dto);

    @Mapping(source = "id.setId", target = "setId")
    @Mapping(source = "id.playerId", target = "playerId")
    SetPlayerDTO toDTO(SetPlayer entity);
}
