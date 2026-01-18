package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.*;
import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import com.michael.AuctionV2.domain.mappers.GameMapper;
import com.michael.AuctionV2.domain.mappers.PlayerMapper;
import com.michael.AuctionV2.domain.mappers.SetPlayerMapper;
import com.michael.AuctionV2.domain.mappers.TeamMapper;
import com.michael.AuctionV2.repositories.GameRepository;
import com.michael.AuctionV2.services.GameService;
import com.michael.AuctionV2.services.PlayerService;
import com.michael.AuctionV2.services.SetService;
import com.michael.AuctionV2.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @GetMapping("/{id}/preview")
    public List<CompletePlayerDTO> showPlayerPreview(@PathVariable("id") Integer gameId){ //TODO Replace setId by GameID the fetch setId from game
        Integer setId =gameService.findById(gameId).getSetId();
        List<CompletePlayerDTO> completePlayerData =
                setService.findAllPlayersOfSet(setId)
                        .stream()
                        .map(setPlayer -> {
                            SetPlayerDTO setPlayerDTO = setPlayerMapper.toDTO(setPlayer);

                            PlayerDTO playerDTO = playerMapper.toDTO(
                                    playerService.findPlayerById(
                                            setPlayer.getId().getPlayerId()
                                    )
                            );

                            return new CompletePlayerDTO(playerDTO, setPlayerDTO);
                        })
                        .toList();
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
    @GetMapping("/{id}/auction/{playerType}")
    public List<CompletePlayerDTO> getAllPlayersOfType(@PathVariable("id")Integer gameId,@PathVariable("playerType") String playerType){
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
        List<Player> playerSet = setService.findAllPlayersOfSet(game.getSetId()).stream()
                .map(player -> playerService.findPlayerById(player.getId().getPlayerId()))
                .filter(player -> player.getType()==typeRequested)
                .toList();
        List<CompletePlayerDTO> result = playerSet.stream().map(player -> {
            SetPlayer setPlayer = setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),player.getId()));
            return new CompletePlayerDTO(
                    playerMapper.toDTO(player),
                    setPlayerMapper.toDTO(setPlayer)
            );
        }).toList();
        return result;
    }
}
