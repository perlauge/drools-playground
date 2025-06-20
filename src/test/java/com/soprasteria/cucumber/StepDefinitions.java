package com.soprasteria.cucumber;

import com.soprasteria.model.Ejendom;
import com.soprasteria.model.EjendomsType;
import com.soprasteria.model.KommuneAarsInformation;
import com.soprasteria.model.RuleContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {

    private Ejendom ejendom;
    private KommuneAarsInformation kommuneAarsInformation;

    @Given("en {string} ejendom i kommune {int}")
    public void ejendom(String ejendomstype, int kommunekode) {
        EjendomsType ejendomsType = EjendomsType.valueOf(ejendomstype);
        this.ejendom = getEjendom();
        this.ejendom.setEjendomsType(ejendomsType);
        this.ejendom.setKommuneKode(kommunekode);
    }

    @Given("kommune {int} med grundskyldspromille {int}, som opkræver dækningsafgift for året {int}")
    public void kommune(int kommunekode, int promille, int aar) {
        this.kommuneAarsInformation = KommuneAarsInformation.builder()
                .kommuneKode(kommunekode)
                .aar(aar)
                .grundskyldspromille(promille)
                .opkraevDaekningsafgift(true).build();
    }


    @When("der genberegnes for år {int}")
    public void genberegnesForYear(int year) {
        ejendom.setSkatteAar(year);
        RuleContext rc = new RuleContext();
        rc.init();
        rc.executeRules(this.ejendom, List.of(this.kommuneAarsInformation));
    }

    @Then("er dækningsafgiften {bigdecimal}")
    public void daekningsafgift(BigDecimal da) {
        assertEquals(da, ejendom.getDaekningsafgift());
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
