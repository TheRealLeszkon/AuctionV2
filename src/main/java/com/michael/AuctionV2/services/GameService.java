package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.dtos.PurchasedPlayer;
import com.michael.AuctionV2.domain.dtos.RefundConfirmation;
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
        //General Checks
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
                .orElseThrow(() -> new IllegalStateException("Player not registered for auction!"));
        if (foundPlayer.getPlayerStatus() == PlayerStatus.SOLD) {
            throw new IllegalStateException("Player already sold");
        }
        if (foundPlayer.getPlayerStatus() != PlayerStatus.FOR_SALE
                && foundPlayer.getPlayerStatus() != PlayerStatus.UNSOLD) {
            throw new IllegalStateException("Player is not available for purchase");
        }


        SetPlayer playerDetails =setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),playerId));
        if(bidAmount.compareTo( playerDetails.getPrice())<0){
            throw new IllegalStateException("Can't sell player below base price!");
        }

        Player playerBioDetails = playerService.findPlayerById(playerDetails.getId().getPlayerId());
        Team team = teamService.getTeamOfAssociationInGame(gameId,teamAssociation);

        // final updates
        teamService.checkConstraintsAndUpdateTeamCounts(
                team,
                playerBioDetails.getType(),
                playerBioDetails.getIsUncapped(),
                game
        );
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
                    log.info("{} <- legend status",playerBioData.getIsLegend());
                    return PurchasedPlayer.builder()
                            .playerType(playerBioData.getType())
                            .name(playerBioData.getName())
                            .boughtFor(purchaseEntry.getSoldPrice())
                            .points(playerGameData.getPoints())
                            .isLegend(playerBioData.getIsLegend())
                            .isForeign(playerBioData.getIsForeign())
                            .isUncapped(playerBioData.getIsUncapped())
                            .build();
                }).toList();
        return teamPurchases;
    }

    @Transactional
    public RefundConfirmation refundPlayer(Integer gameId,Integer playerId){
        Game game = findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        AuctionedPlayer purchaseRecord= auctionedPlayerRepository.findById(new AuctionedPlayerId(game.getId(),playerId))
                .orElseThrow(() ->new IllegalStateException("No such player registered in auction!"));
        if(purchaseRecord.getPlayerStatus()!=PlayerStatus.SOLD){
            throw new IllegalStateException("Not sold players can't be refunded!");
        }
        if (purchaseRecord.getTeamId() == null) {
            throw new IllegalStateException("Refund failed: player is not owned by any team");
        }

        SetPlayer playerGameDetails = setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(), playerId));
        Player playerBioDetails = playerService.findPlayerById(playerId);
        Team team =teamService.findTeamById(purchaseRecord.getTeamId());
        team.setBalance(team.getBalance().add(purchaseRecord.getSoldPrice()));
        team.setPoints(team.getPoints() -playerGameDetails.getPoints());
        teamService.reduceTeamPlayerCounts(team,playerBioDetails);

        purchaseRecord.setTeamId(null);
        purchaseRecord.setSoldPrice(null);
        purchaseRecord.setPlayerStatus(PlayerStatus.UNSOLD);
        return new RefundConfirmation(
                playerBioDetails.getName(),
                purchaseRecord.getPlayerStatus(),
                "Refund was successful!"
        );
    }
}
