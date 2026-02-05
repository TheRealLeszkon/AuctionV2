package com.michael.AuctionV2.domain.entities;

import com.michael.AuctionV2.domain.entities.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game { //TODO add fields here to add game constrains and presets
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer setId;
    @Column(unique = true)
    private String name;
    private GameStatus status;
    //Game Constraints
    private BigDecimal initialBalance ;
    private Integer playersPerTeam;
    private Integer batsmenPerTeam;
    private Integer bowlersPerTeam;
    private Integer allRounderPerTeam;
    private Integer wicketKeeperPerTeam;
    private Integer maxPlayersPerTeam;
    private Integer unCappedPerTeam;
    private Integer legendsPerTeam;
    private Integer specialPlayersPerTeam;
    private Integer foreignPlayersPerTeam;

}
