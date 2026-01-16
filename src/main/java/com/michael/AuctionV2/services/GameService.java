package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.entities.Game;
import com.michael.AuctionV2.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    public Game createGame(Game game){
        return gameRepository.save(game);
    }

    public boolean existsByName(String name){
        return gameRepository.existsByName(name);
    }
    public Game findById(Integer id){
        return gameRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("No Game by Id: "+id+" Exists!"));
    }

}
