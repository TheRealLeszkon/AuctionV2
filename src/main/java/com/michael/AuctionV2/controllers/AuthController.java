package com.michael.AuctionV2.controllers;

import com.michael.AuctionV2.domain.dtos.requests.AuthRequest;
import com.michael.AuctionV2.domain.dtos.responses.PasswordPair;
import com.michael.AuctionV2.domain.entities.Game;
import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.keys.PasswordId;
import com.michael.AuctionV2.services.GamePasswordService;
import com.michael.AuctionV2.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final GameService gameService;
    private final GamePasswordService passwordService;

    @PostMapping
    public Map<String,String> fakeAuth(@RequestBody AuthRequest authRequest){
        Game game = gameService.findById(authRequest.getGameId());
        String username =authRequest.getUsername();
        String password =authRequest.getPassword();
        if(username.equals("host")){
            if(game.getHostPassword().equals(password)){
                return Map.of("message","Host Login Successful!");
            }
            throw new IllegalArgumentException("Login Unsuccessful! Host password Incorrect.");
        }
        else if(username.equals("admin")) {
            if(game.getAdminPassword().equals(password)){
                return Map.of("message","Admin Login Successful!");
            }
            throw new IllegalArgumentException("Login Unsuccessful! Admin password Incorrect.");
        }else {
            IPLAssociation teamAssociation = IPLAssociation.valueOf(username.toUpperCase());
            List<PasswordPair> pairs =gameService.getGameTeamPasswords(game.getId());
            for (PasswordPair passwordPair:pairs){
                if(passwordPair.getAssociation() == teamAssociation && passwordPair.getPassword().equals(password)){
                    return Map.of("message",teamAssociation+" Login Successful!");
                }
            }
            throw new IllegalArgumentException("Login Unsuccessful! " + teamAssociation+ " password Incorrect.");
        }
    }

}
