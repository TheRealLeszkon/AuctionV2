package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.SetDTO;
import com.michael.AuctionV2.domain.dtos.SetPlayerDTO;
import com.michael.AuctionV2.domain.entities.Set;
import com.michael.AuctionV2.domain.entities.SetPlayer;
import com.michael.AuctionV2.domain.mappers.SetMapper;
import com.michael.AuctionV2.domain.mappers.SetPlayerMapper;
import com.michael.AuctionV2.services.SetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/set")
public class SetController {
    private final SetService setService;
    private final SetMapper setMapper;
    private final SetPlayerMapper setPlayerMapper;

    @PostMapping
    public ResponseEntity<SetDTO> createSet(@RequestBody SetDTO set){
        Set savedSet = setService.createSet(setMapper.fromDTO(set));
        SetDTO setDTO = setMapper.toDTO(savedSet);
        return new ResponseEntity<>(setMapper.toDTO(savedSet), HttpStatus.CREATED);
    }
    @GetMapping
    public List<SetDTO> showAllSets(){
        return setService.findAllSets().stream().map(setMapper::toDTO).toList();
    }
    @PostMapping("/{id}")
    public SetPlayerDTO addSetPlayer(@PathVariable("id") Integer setId, @RequestBody SetPlayerDTO addToSetRequest){
        addToSetRequest.setSetId(setId);
        SetPlayer savedSetPlayer = setPlayerMapper.fromDTO(addToSetRequest);
        savedSetPlayer =setService.addPlayerToSet(savedSetPlayer);
        return setPlayerMapper.toDTO(savedSetPlayer);
    }
    @PostMapping("/{id}/bulk")
    public List<SetPlayerDTO> addMultipleSetPlayers(@PathVariable("id") Integer setId, @RequestBody List<SetPlayerDTO> playersToBeAdded){
        playersToBeAdded.forEach(playerToBeAdded -> playerToBeAdded.setSetId(setId));

        List<SetPlayer> savedPlayers =setService.addMultiplePlayersToSet(
                playersToBeAdded.stream()
                .map(setPlayerMapper::fromDTO)
                .toList()
        );
        return savedPlayers.stream().map(setPlayerMapper::toDTO).toList();
    }
    @GetMapping("/{id}")
    public List<SetPlayerDTO> fetchAllPlayersOfSet(@PathVariable("id") Integer setId){
        if(!setService.existsById(setId)){
            throw new IllegalArgumentException("No set of ID: "+ setId+" exists! Can't Fetch non-existent sets!");
        }
        List<SetPlayerDTO> playersOfSet =setService.findAllPlayersOfSet(setId).
                stream().map(setPlayerMapper::toDTO)
                .toList();
        return playersOfSet;
    }

}
