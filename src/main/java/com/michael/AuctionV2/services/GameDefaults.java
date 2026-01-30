package com.michael.AuctionV2.services;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
public final class GameDefaults {
    public static final BigDecimal INITIAL_BALANCE=new BigDecimal("1200000000");
    public static final int PLAYERS_PER_TEAM = 12;
    public static final int BATSMEN_PER_TEAM = 4;
    public static final int BOWLERS_PER_TEAM = 4;
    public static final int ALL_ROUNDER_PER_TEAM = 3;
    public static final int WICKET_KEEPER_PER_TEAM = 1;
    public static final int UNCAPPED_PER_TEAM = 1;
}
