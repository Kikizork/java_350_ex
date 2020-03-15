package com.ipiecoles.java.java350.model;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipiecoles.java.java350.model.Employe;

/**
 * @author frederic.enee Test pour la methode getNombreDeRtt
 */
public class TestEmployeGetNombreDeRtt {

	/**
	 * Test en condition standard avec un employe sans ancienneté
	 * 
	 * @param date
	 * @param nombreDeRttPrevus
	 */
	@ParameterizedTest
	@MethodSource("localDateEmployeDeBase")
	public void testgetNombreDeRttConditionsNormales(LocalDate date, Integer nombreDeRttPrevus) {

		// Given
		Employe employe = new Employe();

		// When
		Integer nombreDeRtt = employe.getNombreDeRttPourUneAnnee(date);

		// Then
		Assertions.assertEquals(nombreDeRttPrevus, nombreDeRtt);
	}

	/**
	 * Test la methode getNombreDeRttAnneeEnCours
	 */
	@Test
	public void testgetNombreDeRttAnneeEnCours() {
		// Given
		Employe employe = new Employe();

		// When
		Integer nombreRtt = employe.getNombreDeRttAnneeEnCours();

		// Then
		Assertions.assertEquals(10, nombreRtt);
	}

	/**
	 * Test condition paramètre date null
	 */
	@Test
	public void testgetNombreDeRttConditionsNull() {
		// Given
		Employe employe = new Employe();

		// When
		NullPointerException nullPointerException = Assertions.assertThrows(NullPointerException.class, () -> {
			employe.getNombreDeRttPourUneAnnee(null);
		});

		// Then
		Assertions.assertEquals("La date ne peut être nulle", nullPointerException.getMessage());
	}

	/**
	 * @param date
	 * @param nombreDeRttPrevus
	 * @param dateEmbauche
	 * @param tempsPartiel
	 * 
	 *                          Test avec ancienneté, test au cas limite
	 *                          l'ancienneté fait passer le nombre de rtt dans les
	 *                          négatifs, test avec temps partiel et ancienneté
	 */
	@ParameterizedTest
	@MethodSource({ "localDateEmployeAvecAnciennete", "localDateEmployeAvecAncienneteHorsLimite",
			"localDateEmployeAvecAncienneteEtTempsPartiel" })
	public void testgetNombreDeRttEmployeAvecAnciennete(LocalDate date, Integer nombreDeRttPrevus,
			LocalDate dateEmbauche, Double tempsPartiel) {

		// Given
		Employe employe = new Employe();
		employe.setDateEmbauche(dateEmbauche);
		employe.setTempsPartiel(tempsPartiel);

		// When
		Integer nombreDeRtt = employe.getNombreDeRttPourUneAnnee(date);

		// Then
		Assertions.assertEquals(nombreDeRttPrevus, nombreDeRtt);
	}

	/**
	 * @return Jeu de test employe sans ancienneté
	 */
	static Stream<Arguments> localDateEmployeDeBase() {
		return Stream.of(Arguments.of(LocalDate.of(2019, 1, 1), 8), Arguments.of(LocalDate.of(2021, 1, 1), 10),
				Arguments.of(LocalDate.of(2022, 1, 1), 10), Arguments.of(LocalDate.of(2032, 1, 1), 11),
				Arguments.of(LocalDate.of(2016, 1, 1), 9), Arguments.of(LocalDate.of(2015, 1, 1), 9));
	}

	/**
	 * @return Jeu de test employe avec ancienneté
	 */
	static Stream<Arguments> localDateEmployeAvecAnciennete() {
		return Stream.of(Arguments.of(LocalDate.of(2019, 1, 1), 5, LocalDate.of(2017, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2021, 1, 1), 6, LocalDate.of(2016, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2022, 1, 1), 9, LocalDate.of(2019, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2032, 1, 1), 11, LocalDate.of(2027, 1, 1), 1.0));
	}

	/**
	 * @return Jeu de test ancienneté mettant le nombre de rtt dans les négatifs
	 */
	static Stream<Arguments> localDateEmployeAvecAncienneteHorsLimite() {
		return Stream.of(Arguments.of(LocalDate.of(2019, 1, 1), 0, LocalDate.of(2010, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2021, 1, 1), 0, LocalDate.of(2005, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2022, 1, 1), 0, LocalDate.of(2010, 1, 1), 1.0),
				Arguments.of(LocalDate.of(2032, 1, 1), 0, LocalDate.of(2005, 1, 1), 1.0));
	}

	/**
	 * @return Jeu de test modifiant le taux de temps partiel
	 */
	static Stream<Arguments> localDateEmployeAvecAncienneteEtTempsPartiel() {
		return Stream.of(Arguments.of(LocalDate.of(2019, 1, 1), 3, LocalDate.of(2017, 1, 1), 0.5),
				Arguments.of(LocalDate.of(2021, 1, 1), 5, LocalDate.of(2016, 1, 1), 0.75),
				Arguments.of(LocalDate.of(2022, 1, 1), 3, LocalDate.of(2019, 1, 1), 0.25),
				Arguments.of(LocalDate.of(2032, 1, 1), 7, LocalDate.of(2027, 1, 1), 0.6));
	}
}
