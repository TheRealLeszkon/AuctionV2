package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.entities.Game;
import com.michael.AuctionV2.domain.entities.GameStatus;
import com.michael.AuctionV2.domain.entities.IPLAssociation;
import com.michael.AuctionV2.domain.entities.Team;
import com.michael.AuctionV2.repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Key;
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

}
