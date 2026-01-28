package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.Player;
import com.michael.AuctionV2.domain.entities.enums.PlayerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Integer> {
    List<Player> findAllByType(PlayerType type);
    boolean existsByName(String fingerPrint);

}
