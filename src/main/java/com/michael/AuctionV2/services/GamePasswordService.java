package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.dtos.responses.PasswordPair;
import com.michael.AuctionV2.domain.entities.GamePassword;
import com.michael.AuctionV2.domain.entities.Team;
import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.keys.PasswordId;
import com.michael.AuctionV2.repositories.GamePasswordRepository;
import com.michael.AuctionV2.repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

@Service
@RequiredArgsConstructor
public class GamePasswordService {
    private final GamePasswordRepository gamePasswordRepository;
    private final TeamRepository teamRepository;

    public void createPasswordsForTeams(Integer gameId , List<Team> teams){
        String[] WORDS = {
                "Tendulkar", "Sachin", "Dhoni", "Kohli", "Kapil",
                "Wasim", "Bradman", "Ricky", "Brian", "Stokes"
        };
        Stack<String> passwords = new Stack<>();
        Random random = new Random();

        while (passwords.size() < 10) {

            String word = WORDS[random.nextInt(WORDS.length)];
            int number = 100 + random.nextInt(900); // 3 digit number

            String password = word + number;
            passwords.add(password);
        }
        List<GamePassword> storedPasswords = new ArrayList<>();
        for (Team team:teams){
            storedPasswords.add(
                    new GamePassword(
                            new PasswordId(gameId, team.getId()) ,
                            passwords.pop()
                    )
            );
        }
        gamePasswordRepository.saveAll(storedPasswords);
    }

    public List<PasswordPair> getPasswordsOfGame(Integer gameId) {

        List<GamePassword> passwords =
                gamePasswordRepository.findByPasswordId_GameId(gameId);

        return passwords.stream()
                .map(gamePassword -> {

                    Integer teamId = gamePassword.getPasswordId().getTeamId();

                    IPLAssociation association = teamRepository.findById(teamId)
                            .map(Team::getAssociation)
                            .orElseThrow(() -> new IllegalStateException("Game Password Retrieval failed!"));

                    return new PasswordPair(
                            association,
                            gamePassword.getPassword()
                    );
                })
                .toList();
    }

}
