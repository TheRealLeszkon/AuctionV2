package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.responses.*;
import com.michael.AuctionV2.domain.dtos.*;
import com.michael.AuctionV2.domain.dtos.requests.PurchaseRequest;
import com.michael.AuctionV2.domain.dtos.requests.RefundRequest;
import com.michael.AuctionV2.domain.dtos.websocket.BidRequest;
import com.michael.AuctionV2.domain.dtos.websocket.WSEvent;
import com.michael.AuctionV2.domain.dtos.websocket.WebSocketEvent;
import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.enums.*;
import com.michael.AuctionV2.domain.mappers.*;
import com.michael.AuctionV2.services.GameService;
import com.michael.AuctionV2.services.PlayerService;
import com.michael.AuctionV2.services.SetService;
import com.michael.AuctionV2.services.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {
    //Preview and start
    private final SetService setService;
    private final PlayerService playerService;

    private final PlayerMapper playerMapper;
    private final SetPlayerMapper setPlayerMapper;

    private final GameService gameService;
    private final GameMapper gameMapper;

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    private final AllRounderStatsMapper allRounderStatsMapper;
    private final BatsmanStatsMapper batsmanStatsMapper;
    private final BowlerStatsMapper bowlerStatsMapper;


    @GetMapping("/{id}/preview")
    public List<CompletePlayer> showPlayerPreview(@PathVariable("id") Integer gameId){ //TODO Replace setId by GameID the fetch setId from game
        Integer setId =gameService.findById(gameId).getSetId();
        List<CompletePlayer> completePlayerData = setService.findAllPlayersOfSet(setId).stream().map(
                playerDetails ->{
                    Player playerBioData =playerService.findPlayerById(playerDetails.getId().getPlayerId());
                    return CompletePlayer.builder()
                            .id(playerBioData.getId())
                            .name(playerBioData.getName())
                            .imageLink(playerBioData.getImageLink())
                            .type(playerBioData.getType())
                            .isLegend(playerBioData.getIsLegend())
                            .isUncapped(playerBioData.getIsUncapped())
                            .country(playerBioData.getCountry())
                            .batsmanStats(batsmanStatsMapper.toDTO(playerBioData.getBatsmenStats()))
                            .bowlerStats(bowlerStatsMapper.toDTO(playerBioData.getBowlerStats()))
                            .allRounderStats(allRounderStatsMapper.toDTO(playerBioData.getAllRounderStats()))
                            .setId(setId)
                            .price(playerDetails.getPrice())
                            .points(playerDetails.getPoints())
                            .order(playerDetails.getOrder())
                            .build();
                }
        ).toList();
        return completePlayerData;
    }

    @PostMapping
    public ResponseEntity<GameDTO> createNewGame(@RequestBody GameDTO gameDTO){
        gameDTO.setStatus(GameStatus.INACTIVE);
        if(gameService.existsByName(gameDTO.getName())){
           throw new IllegalArgumentException("Game of Name: '"+gameDTO.getName()+"' Already Exists!");
        }
        if(!setService.existsById(gameDTO.getSetId())){
            throw new IllegalArgumentException("SetID:"+gameDTO.getSetId()+" Doesn't Exist!");
        }
        GameDTO responseDTO = gameMapper.toDTO(gameService.createGame(gameMapper.fromDTO(gameDTO)));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public GameDTO showGameDetails(@PathVariable("id") Integer gameId){
        return gameMapper.toDTO(gameService.findById(gameId));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameControlMessage> initializeGame(@PathVariable("id") Integer gameId, @RequestBody GameControlMessage controlMessage){ //TODO add END functionality later
        try{
            GameCommand.valueOf(controlMessage.getCommand().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(controlMessage.getCommand()+" is not a command! Did you mean "+GameCommand.START+"?");
        }


        Game game =gameService.InitializeGame(gameId);
        return new ResponseEntity<>(
                GameControlMessage.builder()
                        .message("The game is now active! Team view will now be available.")
                        .gameStatus(game.getStatus())
                        .command(controlMessage.getCommand())
                        .build(),
                HttpStatus.CREATED
        );
    }



    @GetMapping("/{id}/team")
    public List<TeamDTO> fetchAllTeamsInGame(@PathVariable("id") Integer gameId){
        if(gameService.findById(gameId).getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        return teamService.getAllTeamsOfGame(gameId).stream().map(teamMapper::toDTO).toList();
    }

    @GetMapping("/{id}/team/{association}")
    public TeamDTO fetchTeamOfAssociation(@PathVariable("id") Integer gameId,@PathVariable("association") String association){
        if(gameService.findById(gameId).getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        try{
            IPLAssociation.valueOf(association.toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("The Association/Team name in the url is Incorrect! Try in this format: LSG,CSK,DC...");
        }

        IPLAssociation teamAssociation =IPLAssociation.valueOf(association.toUpperCase());
        return teamMapper.toDTO(teamService.getTeamOfAssociationInGame(gameId,teamAssociation));
    }

    @GetMapping("/{id}/team/{association}/purchases")
    public List<PurchasedPlayer> showAllTeamPurchases(@PathVariable("id") Integer gameId, @PathVariable("association") String association){
        Game game = gameService.findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        try{
            IPLAssociation.valueOf(association.toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("The Association/Team name in the url is Incorrect! Try in this format: LSG,CSK,DC...");
        }

        IPLAssociation teamAssociation= IPLAssociation.valueOf(association.toUpperCase());
        Team team = teamService.getTeamOfAssociationInGame(gameId,teamAssociation);
        return gameService.getTeamPurchases(team,game);
    }
    @GetMapping("/{id}/players/{playerType}")
    public List<CompletePlayer> getAllPlayersOfType(@PathVariable("id")Integer gameId,@PathVariable("playerType") String playerType){
        Game game =gameService.findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        try{
            PlayerType.valueOf(playerType.toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Non existent player type in URL! Example player types: BATSMAN,BOWLER,....");
        }
        PlayerType typeRequested =PlayerType.valueOf(playerType.toUpperCase());
        List<CompletePlayer> requestedPlayers = setService.findAllPlayersOfSet(game.getSetId()).stream()
                .map(playerDetails -> {
                    Player playerBioData = playerService.findPlayerById(playerDetails.getId().getPlayerId());
                    PlayerStatus playerStatus = gameService.getPlayerStatusInGame(playerBioData.getId(),game);
                    return CompletePlayer.builder()
                            .id(playerBioData.getId())
                            .name(playerBioData.getName())
                            .imageLink(playerBioData.getImageLink())
                            .type(playerBioData.getType())
                            .isLegend(playerBioData.getIsLegend())
                            .isUncapped(playerBioData.getIsUncapped())
                            .country(playerBioData.getCountry())
                            .batsmanStats(batsmanStatsMapper.toDTO(playerBioData.getBatsmenStats()))
                            .bowlerStats(bowlerStatsMapper.toDTO(playerBioData.getBowlerStats()))
                            .allRounderStats(allRounderStatsMapper.toDTO(playerBioData.getAllRounderStats()))
                            .setId(playerDetails.getId().getSetId())
                            .price(playerDetails.getPrice())
                            .points(playerDetails.getPoints())
                            .order(playerDetails.getOrder())
                            .status(playerStatus)
                            .build();
                })
                .filter(player -> player.getType()==typeRequested)
                .toList();


        return requestedPlayers;
    }

    @GetMapping("/{id}/players")
    public List<CompletePlayer> getAllPlayersOfType(@PathVariable("id")Integer gameId){
        Game game =gameService.findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        List<CompletePlayer> requestedPlayers = setService.findAllPlayersOfSet(game.getSetId()).stream()
                .map(playerDetails -> {
                    Player playerBioData = playerService.findPlayerById(playerDetails.getId().getPlayerId());
                    PlayerStatus playerStatus = gameService.getPlayerStatusInGame(playerBioData.getId(),game);
                    return CompletePlayer.builder()
                            .id(playerBioData.getId())
                            .name(playerBioData.getName())
                            .imageLink(playerBioData.getImageLink())
                            .type(playerBioData.getType())
                            .isLegend(playerBioData.getIsLegend())
                            .isUncapped(playerBioData.getIsUncapped())
                            .country(playerBioData.getCountry())
                            .batsmanStats(batsmanStatsMapper.toDTO(playerBioData.getBatsmenStats()))
                            .bowlerStats(bowlerStatsMapper.toDTO(playerBioData.getBowlerStats()))
                            .allRounderStats(allRounderStatsMapper.toDTO(playerBioData.getAllRounderStats()))
                            .setId(playerDetails.getId().getSetId())
                            .price(playerDetails.getPrice())
                            .points(playerDetails.getPoints())
                            .order(playerDetails.getOrder())
                            .status(playerStatus)
                            .build();
                })
                .toList();
        return requestedPlayers;
    }

    @GetMapping("/{id}/players/unsold")
    public List<CompletePlayer> showAllUnsold(@PathVariable("id") Integer gameId){
        return gameService.getAllPlayersBySoldStatus(gameId,PlayerStatus.UNSOLD);
    }
    @GetMapping("/{id}/players/sold")
    public List<CompletePlayer> showAllSold(@PathVariable("id") Integer gameId){
        return gameService.getAllPlayersBySoldStatus(gameId,PlayerStatus.SOLD);
    }
    @PostMapping("/{id}/purchase")
    public PurchaseConfirmation purchaseAuctionedPlayer(@PathVariable("id")Integer gameId,@RequestBody PurchaseRequest purchaseRequest){
        try{
            IPLAssociation.valueOf(purchaseRequest.getTeamAssociation().toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("The Association/Team name in the url is Incorrect! Try in this format: LSG,CSK,DC...");
        }
        IPLAssociation association =IPLAssociation.valueOf(purchaseRequest.getTeamAssociation().toUpperCase());
        PurchasedPlayer purchasedPlayer =gameService.purchasePlayerForTeam(
                gameId,
                purchaseRequest.getPlayerId(),
                association,
                purchaseRequest.getFinalBid()
        );
        return new PurchaseConfirmation(PlayerStatus.SOLD,association,purchasedPlayer.getBoughtFor());
    }
    @PostMapping("/{id}/refund")
    public RefundConfirmation refundPurchase(@PathVariable("id") Integer gameId, @RequestBody RefundRequest refundRequest) {
        return gameService.refundPlayer(gameId, refundRequest.getPlayerId());
    }

    @GetMapping("{id}/audit")
    public List<GameLog> auditLogs(@PathVariable("id") Integer gameId){
        return gameService.getAllGameLogs(gameId);
    }
    @MessageMapping("/game/{gameId}/bids")
    public void sendCurrentBid(@DestinationVariable Integer gameId, BidRequest request){
        log.info("[websocket]: Bid of {} recieved from game {}",request.getCurrentBid(),gameId);
        gameService.broadcastCurrentBid(request,gameId);
    }
    @GetMapping
    public List<Game> showAllGames(){
        return gameService.getAllGames();
    }


}
