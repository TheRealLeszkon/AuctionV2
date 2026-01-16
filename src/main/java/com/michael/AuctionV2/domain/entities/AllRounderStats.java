package com.michael.AuctionV2.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "allrounder_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllRounderStats {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    private Integer runs;
    private BigDecimal economy;
    private Integer matches;
    private BigDecimal strikeRate;

}
