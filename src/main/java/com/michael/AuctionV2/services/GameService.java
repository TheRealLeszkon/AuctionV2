package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.dtos.PurchasedPlayer;
import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.keys.AuctionedPlayerId;
import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import com.michael.AuctionV2.repositories.AuctionedPlayerRepository;
import com.michael.AuctionV2.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final TeamService teamService;
    private final AuctionedPlayerRepository auctionedPlayerRepository;
    private final  SetService setService;
    private final PlayerService playerService;
    public Game createGame(Game game){
        return gameRepository.save(game);
    }

    public boolean existsByName(String name){
        return gameRepository.existsByName(name);
    }
    public boolean existsById(Integer gameId){
        return gameRepository.existsById(gameId);
    }
    public Game findById(Integer id){
        return gameRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("No Game by Id: "+id+" Exists!"));
    }
    @Transactional
    public Game InitializeGame(Integer gameId){
        Game game = findById(gameId);
        if(game.getStatus()==GameStatus.ACTIVE){
            throw new IllegalStateException("Game ID: "+gameId+" Game Name: "+game.getName()+ " is already active! Can't Initialize Active game!");
        }
        game.setStatus(GameStatus.ACTIVE);

        //Create 10 Teams for the game
        teamService.createTeamsForGame(game);

        //Initalize the player list
        List<AuctionedPlayer> playerToBeRegistered =setService.findAllPlayersOfSet(game.getSetId()).stream().map(setPlayer ->
                    AuctionedPlayer.builder()
                            .auctionedPlayerId(new AuctionedPlayerId(gameId,setPlayer.getId().getPlayerId()))
                            .playerStatus(PlayerStatus.FOR_SALE)
                            .build()
        ).toList();
        auctionedPlayerRepository.saveAll(playerToBeRegistered);
        return game;
    }

    public AuctionedPlayer addAuctionedPlayer(AuctionedPlayer auctionedPlayer){
        return auctionedPlayerRepository.save(auctionedPlayer);
    }
    @Transactional
    public AuctionedPlayer purchasePlayerForTeam(Integer gameId,Integer playerId, IPLAssociation teamAssociation, BigDecimal bidAmount){
        Game game =findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        if(bidAmount.signum()<=0){
            throw new IllegalArgumentException("The bid amount must not be negative or zero!");
        }

        //Check if player already sold
        AuctionedPlayerId auctionedPlayerId =new AuctionedPlayerId(gameId,playerId);
        AuctionedPlayer foundPlayer =auctionedPlayerRepository.findById(auctionedPlayerId)
                .orElseThrow(() -> new IllegalStateException("Player not in set!"));


        if (foundPlayer.getPlayerStatus() == PlayerStatus.SOLD) {
            throw new IllegalStateException("Player already sold");
        }
        if (foundPlayer.getPlayerStatus() != PlayerStatus.FOR_SALE) {
            throw new IllegalStateException("Player is not available for sale");
        }
        SetPlayer playerDetails =setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),playerId));
        Team team = teamService.getTeamOfAssociationInGame(gameId,teamAssociation);

        teamService.updateTeamForPurchase(team,bidAmount,playerDetails.getPoints());
        foundPlayer.setTeamId(team.getId());
        foundPlayer.setSoldPrice(bidAmount);
        foundPlayer.setPlayerStatus(PlayerStatus.SOLD);
        return foundPlayer;
    }

    public List<PurchasedPlayer> getTeamPurchases(Team team,Game game){
        Integer setId =game.getSetId();
        List<AuctionedPlayer> auctionPlayers = auctionedPlayerRepository.findByTeamId(team.getId());
        List<PurchasedPlayer> teamPurchases = auctionPlayers.stream()
                .map(purchaseEntry ->{
                    Integer playerId =purchaseEntry.getAuctionedPlayerId().getPlayerId();
                    Player playerBioData = playerService.findPlayerById(playerId);
                    SetPlayer playerGameData = setService.findPlayerDetailsInSetById(new SetPlayerId(setId,playerId));
                    log.info("{} was fetched!",playerBioData.getName());
                    return new PurchasedPlayer(
                        playerBioData.getName(),
                            playerBioData.getType(),
                            purchaseEntry.getSoldPrice(),
                            playerGameData.getPoints()
                    );
                }).toList();
//        log.info(""teamPurchases.size());
        return teamPurchases;
    }


}
