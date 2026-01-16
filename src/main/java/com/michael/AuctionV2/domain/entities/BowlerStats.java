package com.michael.AuctionV2.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "bowler_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BowlerStats {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    private Integer matches;
    private Integer wickets;
    private BigDecimal economy;
    private String bestFigure;

}
