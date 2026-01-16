package com.michael.AuctionV2.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Named;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Integer id;
    private Integer setId;
    private String name;
}
