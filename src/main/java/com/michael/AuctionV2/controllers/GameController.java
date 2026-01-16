package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.CompletePlayerDTO;
import com.michael.AuctionV2.domain.dtos.GameDTO;
import com.michael.AuctionV2.domain.dtos.PlayerDTO;
import com.michael.AuctionV2.domain.dtos.SetPlayerDTO;
import com.michael.AuctionV2.domain.entities.Player;
import com.michael.AuctionV2.domain.entities.SetPlayer;
import com.michael.AuctionV2.domain.mappers.GameMapper;
import com.michael.AuctionV2.domain.mappers.PlayerMapper;
import com.michael.AuctionV2.domain.mappers.SetPlayerMapper;
import com.michael.AuctionV2.repositories.GameRepository;
import com.michael.AuctionV2.services.GameService;
import com.michael.AuctionV2.services.PlayerService;
import com.michael.AuctionV2.services.SetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        if(gameService.existsByName(gameDTO.getName())){
           throw new IllegalArgumentException("Game of Name: '"+gameDTO.getName()+"' Already Exists!");
        }
        if(!setService.existsById(gameDTO.getSetId())){
            throw new IllegalArgumentException("SetID:"+gameDTO.getSetId()+" Doesn't Exist!");
        }
        GameDTO responseDTO = gameMapper.toDTO(gameService.createGame(gameMapper.fromDTO(gameDTO)));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
