package com.michael.AuctionV2.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "batsmen_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatsmanStats {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    private Integer runs;
    private Integer matches;
    private BigDecimal battingAvg;
    private BigDecimal strikeRate;

}
