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

    public static final int FOREIGN_PER_TEAM = 2;

    public static final int LEGENDS_PER_TEAM = 1;

    public static final int SPECIAL_PER_TEAM = 1;

    public static final int SUBSTITUTES_PER_TEAM = 3;

    public static final int MAX_FOREIGN_ALLOWED = 3;

    public static final String HOST_PASS = "gta6";
    public static final String ADMIN_PASS = "password";
}
