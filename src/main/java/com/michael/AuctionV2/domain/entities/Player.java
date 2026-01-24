package com.michael.AuctionV2.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cricketers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String imageLink;
    @Enumerated(EnumType.STRING)
    private PlayerType type;
    private Boolean isForeign;
    private Boolean isUncapped;
    private Boolean isLegend;
    private String country;

    @OneToOne(mappedBy = "player",cascade = CascadeType.ALL)
    private BatsmanStats batsmenStats;
    @OneToOne(mappedBy = "player",cascade = CascadeType.ALL)
    private BowlerStats bowlerStats;
    @OneToOne(mappedBy = "player",cascade = CascadeType.ALL)
    private AllRounderStats allRounderStats;

}
