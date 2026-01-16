package com.michael.AuctionV2.domain.entities.keys;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class SetPlayerId implements Serializable {
    private Integer setId;
    private Integer playerId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SetPlayerId that = (SetPlayerId) o;
        return Objects.equals(setId, that.setId) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setId, playerId);
    }
}
