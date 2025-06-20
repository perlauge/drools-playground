package com.soprasteria.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MyTest {
    static final Logger LOG = LoggerFactory.getLogger(MyTest.class);

    RuleContext ruleContext;

    @BeforeEach
    void init() {
        ruleContext = new RuleContext();
        ruleContext.init();
    }

    @Test
    void testUnder50ProcentErhverv() {
        LOG.info("Now running data");

        Ejendom ejendom = getEjendom();
        ejendom.setForskelsvaerdiAnvendt(BigDecimal.valueOf(500_000)); // halvdelen af ejendomsværdi.

        List<KommuneAarsInformation> kommuneAarsInformationer = new LinkedList<>();
        kommuneAarsInformationer.add(KommuneAarsInformation.builder()
                .kommuneKode(1337)
                .aar(2021)
                .grundskyldspromille(3)
                .opkraevDaekningsafgift(false).build());

        ruleContext.executeRules(ejendom, kommuneAarsInformationer);

        assertFalse(ejendom.isMereEnd50ProcentErhverv());
        LOG.info("rules executed for ejendom {}", ejendom);

    }

    @Test
    void testSimple() {
        LOG.info("Now running data");
        Ejendom ejendom = getEjendom();

        List<KommuneAarsInformation> kommuneAarsInformationer = new LinkedList<>();
        kommuneAarsInformationer.add(KommuneAarsInformation.builder()
                .kommuneKode(1337)
                .aar(2021)
                .grundskyldspromille(3)
                .opkraevDaekningsafgift(true).build());
        kommuneAarsInformationer.add(KommuneAarsInformation.builder()
                .kommuneKode(1337)
                .aar(2022)
                .grundskyldspromille(4)
                .opkraevDaekningsafgift(false).build());
        kommuneAarsInformationer.add(KommuneAarsInformation.builder()
                .kommuneKode(1337)
                .aar(2020)
                .grundskyldspromille(2)
                .opkraevDaekningsafgift(true).build());

        ruleContext.executeRules(ejendom, kommuneAarsInformationer);

        assertTrue(ejendom.isMereEnd50ProcentErhverv());
        assertEquals(BigDecimal.valueOf(2250), ejendom.getDaekningsafgift());
        LOG.info("rules executed for ejendom {}", ejendom.getRules());

    }

    private static Ejendom getEjendom() {
        Ejendom ejendom = Ejendom.builder()
                .ejendomsvaerdi(BigDecimal.valueOf(3_000_000))
                .grundvaerdi(BigDecimal.valueOf(2_000_000))
                .fritagelse(BigDecimal.valueOf(200_000))
                .skatteAar(2021)
                .forskelsvaerdiAnvendt(BigDecimal.valueOf(500_001))
                .ejendomsType(EjendomsType.AAR2)
                .kommuneKode(1337).build();
        ejendom.setForskelsvaerdi(ejendom.getEjendomsvaerdi().subtract(ejendom.getGrundvaerdi()));
        return ejendom;
    }

}