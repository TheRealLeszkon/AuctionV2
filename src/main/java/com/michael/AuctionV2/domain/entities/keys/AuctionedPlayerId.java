package com.michael.AuctionV2.domain.entities.keys;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionedPlayerId implements Serializable {
    private Integer gameId;
    private Integer playerId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuctionedPlayerId that = (AuctionedPlayerId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, playerId);
    }
}
