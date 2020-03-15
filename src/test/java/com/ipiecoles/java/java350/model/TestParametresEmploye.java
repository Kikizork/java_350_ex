package com.ipiecoles.java.java350.model;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipiecoles.java.java350.model.Employe;

public class TestParametresEmploye {

	@ParameterizedTest
	@MethodSource("matriculePerformancePrimeTempsPartiel")
	public void parameterizedTestGetPrimeAnnuelle(String matricule, Integer performance, LocalDate dateEmbauche,
			Double prime, Double tempsPartiel) {
		// Given
		Employe employe = new Employe();
		employe.setDateEmbauche(dateEmbauche);
		employe.setPerformance(performance);
		employe.setMatricule(matricule);
		employe.setTempsPartiel(tempsPartiel);
		// When
		Double primeAnnuelle = employe.getPrimeAnnuelle();

		// Then
		Assertions.assertThat(primeAnnuelle).isEqualTo(prime);
	}

	static Stream<Arguments> matriculePerformancePrimeTempsPartiel() {

		return Stream.of(Arguments.of(null, null, null, 1000d, null), Arguments.of("C00000", 1, null, 1000d, null),
				Arguments.of("0", 2, LocalDate.now().minusYears(2), 2500d, null),
				Arguments.of("0", 2, LocalDate.now().minusYears(2), 1250d, 0.5),
				Arguments.of("0", 2, LocalDate.now().minusYears(2), 2500d, -1d),
				Arguments.of("M00000", 2, LocalDate.now().minusYears(2), 1900d, -1d));

	}
}
