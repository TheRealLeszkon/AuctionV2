package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.GameTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameTransactionRepository extends JpaRepository<GameTransaction,Integer> {
        List<GameTransaction> findAllGameTransactionByGameIdOrderByIdDesc(Integer gameId);
}
