package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordPair {
    IPLAssociation association;
    String password;
}
