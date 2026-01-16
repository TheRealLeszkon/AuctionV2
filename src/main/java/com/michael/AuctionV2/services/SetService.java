package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.dtos.SetPlayerDTO;
import com.michael.AuctionV2.domain.entities.Player;
import com.michael.AuctionV2.domain.entities.Set;
import com.michael.AuctionV2.domain.entities.SetPlayer;
import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import com.michael.AuctionV2.repositories.SetPlayersRepository;
import com.michael.AuctionV2.repositories.SetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetService {
    private final SetRepository setRepository;
    private final SetPlayersRepository setPlayersRepository;
    private final PlayerService playerService;

    public Set createSet(Set set){
        if (setRepository.existsByName(set.getName())){
            throw new IllegalArgumentException("Set of Name: "+set.getName()+" Already exists! Pick a new name.");
        }
        return setRepository.save(set);
    }
    public List<Set> findAllSets(){
        return setRepository.findAll();
    }
    public Optional<Set> findSetById(Integer setId){
        return setRepository.findById(setId);
    }

    public boolean existsById(Integer setId){
        return setRepository.existsById(setId);
    }
    public SetPlayer addPlayerToSet(SetPlayer setPlayer){
        SetPlayerId setPlayerId =setPlayer.getId(); //Composite P-key
        Integer setId =setPlayerId.getSetId();
        Integer playerId = setPlayerId.getPlayerId();
        if (setPlayersRepository.existsById(setPlayer.getId())) {
            throw new IllegalStateException(
                    "Player " + playerId + " is already assigned to set " + setId
            );
        }

        //Check if set and player exists before assigning
        findSetById(setId).orElseThrow(() ->new IllegalArgumentException("There is no setID =" + setId + "! Can't assign a player to a set that doesn't exist!"));
        playerService.findPlayerById(playerId); //TODO Replace with existsById() later

        return setPlayersRepository.save(setPlayer);
    }
    @Transactional
    public List<SetPlayer> addMultiplePlayersToSet(List<SetPlayer> setPlayers){
        List<SetPlayer> savedResults =new ArrayList<>();
        for(SetPlayer setPlayer:setPlayers){
            savedResults.add(addPlayerToSet(setPlayer));
        }
        return savedResults;
    }
    public List<SetPlayer> findAllPlayersOfSet(Integer setId){
        return setPlayersRepository.findAllByIdSetId(setId);
    }

}
