package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.dtos.responses.PasswordPair;
import com.michael.AuctionV2.domain.entities.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Integer id;
    private Integer setId;
    private String name;
    private GameStatus status;

    //Game Constraints
    private BigDecimal initialBalance;
    private Integer playersPerTeam;
    private Integer batsmenPerTeam;
    private Integer bowlersPerTeam;
    private Integer allRounderPerTeam;
    private Integer wicketKeeperPerTeam;
    private Integer substitutesPerTeam;
    private Integer unCappedPerTeam;
    private Integer legendsPerTeam;
    private Integer specialPlayersPerTeam;
    private Integer foreignPlayersPerTeam;
    private Integer maxForeignAllowed;

    //passwords
    private String adminPassword;
    private String hostPassword;
    private List<PasswordPair> teamPasswords;
}
