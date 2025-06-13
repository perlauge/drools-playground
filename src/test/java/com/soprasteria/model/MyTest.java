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

    KieServices kieServices;

    KieContainer kContainer;
    KieBase kieBase;

    @BeforeEach
    void init() {
        kieServices = KieServices.Factory.get();

        kContainer = kieServices.getKieClasspathContainer();
        Results verifyResults = kContainer.verify();
        for (Message m : verifyResults.getMessages()) {
            LOG.info("{}", m);
        }

        LOG.info("Creating kieBase");
        kieBase = kContainer.getKieBase();

        LOG.info("There should be rules: ");
        for (KiePackage kp : kieBase.getKiePackages()) {
            for (Rule rule : kp.getRules()) {
                LOG.info("kp {} rule {}", kp, rule.getName());
            }
        }

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

        executeRules(ejendom, kommuneAarsInformationer);

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

        executeRules(ejendom, kommuneAarsInformationer);

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


    private KieSession getSession() {
        LOG.info("Creating kieSession");
        return kieBase.newKieSession();
    }

    public void executeRules(Ejendom ejendom, List<KommuneAarsInformation> kommuneAarsInformationer) {
        KieSession session = getSession();
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();

        session.addEventListener(agendaEventListener);
        session.insert(ejendom);
        kommuneAarsInformationer.forEach(session::insert);
        LOG.info("firing all Rules");
        session.fireAllRules();
        LOG.info("");
        LOG.info("activations: {}", agendaEventListener.getMatchList());
        List<Match> activations = agendaEventListener.getMatchList();
        for (Match match : activations) {
            LOG.info("match: {}", match);
        }

    }


}