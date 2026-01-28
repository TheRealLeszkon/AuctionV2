package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.enums.PlayerType;
import com.michael.AuctionV2.repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    public final static Map<IPLAssociation,String> associationMap = Map.of(
            IPLAssociation.CSK,"Chennai Super Kings",
            IPLAssociation.DC,"Delhi Capital",
            IPLAssociation.GT,"Gujarat Titans",
            IPLAssociation.KKR,"Kolkata Knight Riders",
            IPLAssociation.LSG,"Lucknow Super Giants",
            IPLAssociation.MI,"Mumbai Indians",
            IPLAssociation.PBKS,"Punjab Super Kings",
            IPLAssociation.RR,"Rajasthan Royals",
            IPLAssociation.RCB,"Royal Challengers Bengaluru",
            IPLAssociation.SRH,"Sunrisers Hyderabad"
    );

    public void createTeamsForGame(Game game){
        List<Team> readyToSave = new ArrayList<>();
        associationMap.forEach(
                (key,value)->readyToSave.add(
                        Team.builder()
                                .association(key)
                                .name(value)
                                .gameId(game.getId())
                                .points(0)
                                .playerCount(0)
                                .allRounderCount(0)
                                .batsmanCount(0)
                                .bowlerCount(0)
                                .wicketKeeperCount(0)
                                .uncappedCount(0)
                                .balance(game.getInitialBalance())
                                .build()
                ));
        teamRepository.saveAll(readyToSave);
    }

    public Team getTeamOfAssociationInGame(Integer gameId, IPLAssociation association){
        return teamRepository.findByGameIdAndAssociation(gameId,association);
    }
    public List<Team> getAllTeamsOfGame(Integer gameId){
        return  teamRepository.findAllByGameId(gameId);
    }

    public void updateTeamForPurchase(Team team,BigDecimal amount,Integer points){
        if(team.getBalance().compareTo(amount)<0){
            throw new IllegalArgumentException("The team "+team.getAssociation()+" has insufficient funds to purchase this player! Current team balance : "+team.getBalance());
        }
        team.setBalance(team.getBalance().subtract(amount));
        team.setPoints(team.getPoints()+points);
    }
    public void checkConstraintsAndUpdateTeamCounts(
            Team team, PlayerType playerType, boolean isUncapped, Game game

    ){
        Integer maxPlayerCount = game.getPlayersPerTeam();
        Integer maxAllRounderCount =game.getAllRounderPerTeam();
        Integer maxBatsmanCount =game.getBatsmenPerTeam();
        Integer maxBowlerCount =game.getBowlersPerTeam();
        Integer maxWicketKeeperCount =game.getWicketKeeperPerTeam();
        Integer maxUncappedCount =game.getUnCappedPerTeam();
        if(team.getPlayerCount()>=maxPlayerCount){
            throw new IllegalStateException("Maximum number of players Reached!");
        }
        if(isUncapped){
            if(team.getUncappedCount()>=maxUncappedCount){
                throw new IllegalStateException("Maximum number of uncapped Players Reached!");
            }
        }
        switch (playerType){
            case BATSMAN ->{
                if (team.getBatsmanCount()>=maxBatsmanCount){
                    throw new IllegalStateException("Maximum number of batsmen already reached!");
                }
                team.setBatsmanCount(team.getBatsmanCount()+1);
            }
            case BOWLER ->{
                if(team.getBowlerCount()>=maxBowlerCount){
                    throw  new IllegalStateException("Maximum number of bowlers already reached!");
                }
                team.setBowlerCount(team.getBowlerCount()+1);
            }
            case ALL_ROUNDER ->{
                if(team.getAllRounderCount()>= maxAllRounderCount){
                    throw new IllegalStateException("Maximum number of all rounders already reached!");
                }
                team.setAllRounderCount(team.getAllRounderCount()+1);
            }
            case WICKET_KEEPER ->{
                if(team.getWicketKeeperCount()>=maxWicketKeeperCount){
                    throw new IllegalStateException("Maximum number of wicket keepers already reached!");
                }
                team.setWicketKeeperCount(team.getWicketKeeperCount()+1);
            }
        }
        if(isUncapped) team.setUncappedCount(team.getUncappedCount()+1);
        team.setPlayerCount(team.getPlayerCount()+1);

    }

    public Team findTeamById(Integer teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new IllegalArgumentException("Team not found!"));
    }
    public void reduceTeamPlayerCounts(Team team,Player playerBioDetails) {
        if (team.getPlayerCount() <= 0) {
            throw new IllegalStateException("Team doesn't have any purchases to refund!");
        }

        if(playerBioDetails.getIsUncapped()){
            if (team.getUncappedCount() <= 0) {
                throw new IllegalStateException("Uncapped count already zero");
            }
            team.setUncappedCount(team.getUncappedCount()-1);
        }
        switch(playerBioDetails.getType()){
            case BATSMAN ->{
                if (team.getBatsmanCount() <= 0) {
                    throw new IllegalStateException("Batsman count already zero");
                }
                team.setBatsmanCount(team.getBatsmanCount() -1);
            }
            case WICKET_KEEPER -> {
                if (team.getWicketKeeperCount() <= 0) {
                    throw new IllegalStateException("Wicket keeper count already zero");
                }
                team.setWicketKeeperCount(team.getWicketKeeperCount()-1);
            }
            case ALL_ROUNDER -> {
                if (team.getAllRounderCount() <= 0) {
                    throw new IllegalStateException("All Rounder count already zero");
                }
                team.setAllRounderCount(team.getAllRounderCount()-1);
            }
            case BOWLER -> {
                if (team.getBowlerCount() <= 0) {
                    throw new IllegalStateException("Bowler count already zero");
                }
                team.setBowlerCount(team.getBowlerCount()-1);
            }
        }
        team.setPlayerCount(team.getPlayerCount()-1);
    }
}
