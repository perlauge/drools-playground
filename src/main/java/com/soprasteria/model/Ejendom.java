package com.soprasteria.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

/**
 * Indeholder beregningsinformation relateret til en ejendom for et givet skatteår.
 * Kommunkode/skatteår er relation til KommuneAarsInformation for de kommunalt relevante beregningsdata.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Ejendom {

	private EjendomsType ejendomsType;
	private BigDecimal ejendomsvaerdi;
	private BigDecimal grundvaerdi;
	private BigDecimal forskelsvaerdi;
	private BigDecimal forskelsvaerdiAnvendt;
	private BigDecimal fritagelse;
	private BigDecimal daekningsafgiftspligtigForskelsvaerdi;
	private BigDecimal regel50procent;
	private BigDecimal forskelsvaerdiEfter50kRegel;
	private BigDecimal daekningsafgift;
	private int kommuneKode;
	private int skatteAar;
	@Builder.Default
	private boolean mereEnd50ProcentErhverv = false;


	//--------------
	// blot for sporbarhed, så kan reglerne tilføje tekstdata til hvilke regler, der er blevet anvendt.
	@Builder.Default
	private List<String> rules = new ArrayList<>();

	public void addRule(String rule) {
		this.rules.add(rule);
	}

	public String getRules() {
		return String.join("\n", rules);
	}

}
