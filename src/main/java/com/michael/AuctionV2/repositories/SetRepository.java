package com.michael.AuctionV2.repositories;

import com.michael.AuctionV2.domain.entities.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetRepository extends JpaRepository<Set,Integer> {
    public boolean existsByName(String name);
}
