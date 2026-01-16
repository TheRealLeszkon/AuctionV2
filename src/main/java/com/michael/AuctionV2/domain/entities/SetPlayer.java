package com.michael.AuctionV2.domain.entities;

import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPlayer {
    @EmbeddedId
    private SetPlayerId id;
    private Integer points;
    private BigDecimal price;
    @Column(name = "player_order")
    private Integer order;
}
