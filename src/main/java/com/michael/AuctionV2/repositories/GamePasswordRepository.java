package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.GamePassword;
import com.michael.AuctionV2.domain.entities.keys.PasswordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamePasswordRepository extends JpaRepository<GamePassword, PasswordId> {
    List<GamePassword> findByPasswordId_GameId(Integer gameId);
}
