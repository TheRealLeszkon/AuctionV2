package com.michael.AuctionV2.domain.entities;

import com.michael.AuctionV2.domain.entities.enums.PlayerStatus;
import com.michael.AuctionV2.domain.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "game_transactions")
@Builder
public class GameTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;
    private LocalDateTime time;

    @PrePersist
    public void setTime(){
        this.time = LocalDateTime.now();
    }
}
