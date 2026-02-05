package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.enums.PlayerType;
import com.michael.AuctionV2.repositories.AllRounderStatsRepository;
import com.michael.AuctionV2.repositories.BatsmanStatsRepository;
import com.michael.AuctionV2.repositories.BowlerStatsRepository;
import com.michael.AuctionV2.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final AllRounderStatsRepository allRounderStatsRepository;
    private final BatsmanStatsRepository batsmanStatsRepository;
    private final BowlerStatsRepository bowlerStatsRepository;

    public Player createPlayerWithStats(Player player){
        //TODO Save stats and player data
        if(player.getBatsmenStats() == null && player.getBowlerStats() == null && player.getAllRounderStats() == null){
            throw new IllegalArgumentException("The Player Doesn't have any associated stats! Player Name:"+player.getName()+" Type:"+player.getType());
        }
        if(playerRepository.existsByName(player.getName())){ //TODO Remove condition in prod (Maybe)
            throw new IllegalArgumentException("The player "+ player.getName()+":"+player.getType()+" already Exists in DB!");
        }

        switch (player.getType()){
            case BATSMAN,WICKET_KEEPER ->{
                BatsmanStats stats = player.getBatsmenStats();
                stats.setPlayer(player);
                player.setBatsmenStats(stats);
            }
            case BOWLER -> {
                BowlerStats stats = player.getBowlerStats();
                stats.setPlayer(player);
                player.setBowlerStats(stats);
            }
            case ALL_ROUNDER -> {
                AllRounderStats stats = player.getAllRounderStats();
                stats.setPlayer(player);
                player.setAllRounderStats(stats);
            }
        }



        return playerRepository.save(player);
    }

    public Player findPlayerByName(String name){
        return playerRepository.findPlayerByName(name);
    }

    @Transactional
    public List<Player> createMultiplePlayersWithStats(List<Player> players){
        List<Player> savedResults = new ArrayList<>();
        for(Player player: players){
            savedResults.add(createPlayerWithStats(player));
        }
        return savedResults;
    }

    public List<Player> findAllPlayers(){
        List<Player> players =playerRepository.findAll();
        players.forEach(player -> {
            switch (player.getType()){
                case BATSMAN, WICKET_KEEPER -> player.setBatsmenStats(
                        batsmanStatsRepository
                                .findById(player.getId())
                                .orElse(null)
                );
                case BOWLER -> player.setBowlerStats(
                        bowlerStatsRepository.findById(player.getId())
                                .orElse(null)

                );
                case ALL_ROUNDER -> player.setAllRounderStats(allRounderStatsRepository
                        .findById(player.getId())
                        .orElse(null)
                );
            }
        });
        return players;
    }
    public List<Player> findAllOfPlayerType(PlayerType type){
        List<Player> foundPlayers = playerRepository.findAllByType(type);
        return foundPlayers;
    }

    public Player findPlayerById(Integer playerId) {
        return playerRepository.findById(playerId).orElseThrow(() -> new IllegalArgumentException("No Player of ID: "+playerId+" exists in DB!"));
    }

}
