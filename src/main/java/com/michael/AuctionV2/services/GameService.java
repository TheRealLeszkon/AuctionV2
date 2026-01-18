package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.entities.Game;
import com.michael.AuctionV2.domain.entities.GameStatus;
import com.michael.AuctionV2.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final TeamService teamService;
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
        teamService.createTeamsForGame(game);
        return game;
    }


}
