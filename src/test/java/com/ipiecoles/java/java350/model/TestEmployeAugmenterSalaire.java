package com.ipiecoles.java.java350.model;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;

/**
 * @author frederic.enee
 *
 *         Class pour tester la fonction augmenter salaire de la classe Employe
 */

public class TestEmployeAugmenterSalaire {

	/**
	 * 
	 * Test la fonction avec la valeur par défaut du salaire
	 * 
	 * @throws NullPointerException
	 * @throws EmployeException
	 */
	@Test
	public void testAugmenterSalaireAvecSalaireDeBase() throws NullPointerException, EmployeException {

		// Given
		Employe employe = new Employe();

		// When
		employe.augmenterSalaire(25.0);

		// Then
		// Valeur salaire par défaut : 1521.22 + 25% = 1901.525
		Assertions.assertEquals(1901.525, employe.getSalaire());
	}

	/**
	 * 
	 * Test la fonction avec des valeurs attendues normales
	 * 
	 * @param pourcentage
	 * @param salaireEmploye
	 * @param salaireAttendu
	 * @throws NullPointerException
	 * @throws EmployeException
	 */
	@ParameterizedTest
	@MethodSource("employeTestingConditionNormales")
	public void testAugmenterSalaireConditionNormales(Double pourcentage, Double salaireEmploye, Double salaireAttendu)
			throws NullPointerException, EmployeException {

		// Given
		Employe employe = new Employe();
		employe.setSalaire(salaireEmploye);

		// When
		employe.augmenterSalaire(pourcentage);

		// Then
		Assertions.assertEquals(employe.getSalaire(), salaireAttendu);

	}

	/**
	 * 
	 * Test la fonction avec des paramètres nuls
	 * 
	 * @param pourcentage
	 * @param salaireEmploye
	 * @param exception
	 */
	@ParameterizedTest
	@MethodSource("employeTestingConditionNulles")
	public void testAugmenterSalaireConditionNull(Double pourcentage, Double salaireEmploye, String exception) {
		// Given
		Employe employe = new Employe();
		employe.setSalaire(salaireEmploye);

		// When
		NullPointerException nullPointerException = Assertions.assertThrows(NullPointerException.class, () -> {
			employe.augmenterSalaire(pourcentage);
		});

		// Then
		Assertions.assertEquals(nullPointerException.getMessage(), exception);

	}

	/**
	 * Test les limites avec des valeurs négatives qui ne font pas sens
	 * 
	 * @param pourcentage
	 * @param salaireEmploye
	 * @param exception
	 */
	@ParameterizedTest
	@MethodSource("employeTestingConditionNegatives")
	public void testAugmenterSalaireConditionNegative(Double pourcentage, Double salaireEmploye, String exception) {

		Employe employe = new Employe();
		employe.setSalaire(salaireEmploye);
		EmployeException e = Assertions.assertThrows(EmployeException.class, () -> {
			employe.augmenterSalaire(pourcentage);
		});

		Assertions.assertEquals(e.getMessage(), exception);
	}

	/**
	 * @return Stream d'arguments testant les conditions nulles
	 */
	static Stream<Arguments> employeTestingConditionNulles() {
		return Stream.of(Arguments.of(null, null, "Erreur, le salaire de l'employe est null"),
				Arguments.of(null, 2000.36, "Erreur, le paramètre pourcentage est null"));

	}

	/**
	 * 
	 * @return Stream d'arguments testant les conditions négatives
	 */
	static Stream<Arguments> employeTestingConditionNegatives() {
		return Stream.of(Arguments.of(15.0, -2000.5, "Erreur, le salaire de l'employe est négatif"),

				Arguments.of(-5.2, 2000.25, "Erreur, le pourcentage d'augmentation est inférieur à zéro"));
	}

	/**
	 * @return Stream d'arguments testant les conditions standards
	 */
	static Stream<Arguments> employeTestingConditionNormales() {
		return Stream.of(Arguments.of(0D, 0D, 0D), Arguments.of(0D, 2000.87, 2000.87), Arguments.of(10D, 2000D, 2200D));
	}
}
