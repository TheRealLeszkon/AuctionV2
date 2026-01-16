package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.BatsmanStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatsmanStatsRepository extends JpaRepository<BatsmanStats,Integer> {
}
