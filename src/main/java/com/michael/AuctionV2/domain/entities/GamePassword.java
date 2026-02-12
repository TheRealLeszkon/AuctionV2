package com.michael.AuctionV2.domain.entities;

import com.michael.AuctionV2.domain.entities.keys.PasswordId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_passwords")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePassword {
    @EmbeddedId
    private PasswordId passwordId;
    private String password;
}
