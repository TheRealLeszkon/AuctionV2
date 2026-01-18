package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.SetPlayer;
import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetPlayersRepository extends JpaRepository<SetPlayer, SetPlayerId> {
    public List<SetPlayer> findAllByIdSetIdOrderByOrder(Integer setId);
}
