package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.PlayerDTO;
import com.michael.AuctionV2.domain.entities.AllRounderStats;
import com.michael.AuctionV2.domain.entities.BowlerStats;
import com.michael.AuctionV2.domain.entities.Player;
import com.michael.AuctionV2.domain.entities.PlayerType;
import com.michael.AuctionV2.domain.mappers.PlayerMapper;
import com.michael.AuctionV2.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerMapper playerMapper;
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> showPlayersAvailable(){
        List<Player> players =playerService.findAllPlayers();
        List<PlayerDTO> playerDTOS = players.stream().map(playerMapper::toDTO).toList();
        return new ResponseEntity<>(playerDTOS, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<PlayerDTO> createNewPlayer(@RequestBody PlayerDTO playerDTO){
        Player savedPlayer =playerService.createPlayerWithStats(playerMapper.fromDTO(playerDTO));
        PlayerDTO responseDTO = playerMapper.toDTO(savedPlayer);
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<PlayerDTO>> createNewPlayers(@RequestBody List<PlayerDTO> playerDTOS){
        //TODO
        List<Player> savedPlayers = playerService.createMultiplePlayersWithStats(playerDTOS.stream().map(playerMapper::fromDTO).toList());
        List<PlayerDTO> responseDTOs = savedPlayers.stream().map(playerMapper::toDTO).toList();
        return new ResponseEntity<>(responseDTOs,HttpStatus.CREATED);
    }
    @GetMapping("/{type}")
    public ResponseEntity<?> showPlayersOfType(@PathVariable String type){ //List<PlayerDTO> return type
        try{
            PlayerType PType = PlayerType.valueOf(type.toUpperCase());
            List<PlayerDTO> playerDTOS = playerService.findAllOfPlayerType(PType).stream().map(playerMapper::toDTO).toList();
            return new ResponseEntity<>(playerDTOS,HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            log.info("Client fetch player of Type Error! There is no such type '{}'",type.toUpperCase());
        }
        return ResponseEntity.badRequest()
                .body(Map.of("error","Invalid player type in URL!","provided",type.toUpperCase(),"allowed",List.of("BATSMAN","BOWLER","WICKET_KEEPER","ALL_ROUNDER")));

    }

}
