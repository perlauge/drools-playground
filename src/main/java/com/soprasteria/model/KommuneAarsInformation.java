package com.soprasteria.model;

import lombok.*;


/**
 * Indeholder årsinformation for en enkelt kommune, dvs. grundskyldspromillen og om kommunen opkræver dækningsafgift eller ej.
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class KommuneAarsInformation {
    private int kommuneKode;
    private int aar;
    private int grundskyldspromille;
    private boolean opkraevDaekningsafgift;
}
