package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.AuctionedPlayer;
import com.michael.AuctionV2.domain.entities.keys.AuctionedPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionedPlayerRepository extends JpaRepository<AuctionedPlayer, AuctionedPlayerId> {
}
