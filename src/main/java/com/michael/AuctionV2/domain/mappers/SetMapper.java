package com.michael.AuctionV2.domain.mappers;

import com.michael.AuctionV2.domain.dtos.SetDTO;
import com.michael.AuctionV2.domain.entities.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SetMapper {
    Set fromDTO(SetDTO setDTO);
    SetDTO toDTO(Set set);
}
