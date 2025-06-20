package com.soprasteria.model;

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

import java.util.List;

public class RuleContext {
    static final Logger LOG = LoggerFactory.getLogger(MyTest.class);

    KieServices kieServices;

    KieContainer kContainer;
    KieBase kieBase;

    public void init() {
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
