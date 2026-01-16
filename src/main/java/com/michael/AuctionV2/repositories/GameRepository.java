package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game,Integer> {
    boolean existsByName(String name);
}
