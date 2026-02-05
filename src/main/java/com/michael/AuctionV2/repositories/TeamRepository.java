package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Integer> {
    List<Team> findAllByGameId(Integer gameId);

    List<Team> findAllByGameIdOrderByPointsDesc(Integer gameId);

    Integer countByGameIdAndSelectionLockedTrue(Integer gameId);
    Team findByGameIdAndAssociation(Integer gameId, IPLAssociation association);
}
