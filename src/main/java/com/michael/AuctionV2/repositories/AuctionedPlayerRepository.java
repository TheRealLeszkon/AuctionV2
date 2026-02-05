package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.AuctionedPlayer;
import com.michael.AuctionV2.domain.entities.enums.PlayerStatus;
import com.michael.AuctionV2.domain.entities.keys.AuctionedPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionedPlayerRepository extends JpaRepository<AuctionedPlayer, AuctionedPlayerId> {
    List<AuctionedPlayer> findByTeamId(Integer teamId);

    List<AuctionedPlayer> findAllByAuctionedPlayerIdInAndTeamId(List<AuctionedPlayerId> auctionedPlayerIds,Integer teamId);
    List<AuctionedPlayer> findAllByPlayerStatusOrderByAuctionedPlayerId(PlayerStatus status);
}
