package com.ipiecoles.java.java350.service;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;

@SpringBootTest
public class EmployeServiceTestEmbaucheEmploye {

	@Autowired
	EmployeService employeService;

	@Autowired
	EmployeRepository employeRepository;

	@BeforeEach
	public void remiseAZero() {
		employeRepository.deleteAll();
	}

	@Test
	public void testEmployeServiceEmbaucheEmployeMatriculeSuperieurA90000() throws EmployeException {
		// Given
		Employe lastEmploye = new Employe();
		lastEmploye.setMatricule("C91000");
		employeRepository.save(lastEmploye);
		// When
		employeService.embaucheEmploye("Test", "Test", Poste.COMMERCIAL, NiveauEtude.BTS_IUT, 1.0);

		// Then
		Assertions.assertEquals("91001", employeRepository.findLastMatricule());
	}

	@Test
	public void testEmployeServiceEmbaucheEmployeMatriculeSuperieurA100000() {
		// Given
		Employe lastEmploye = new Employe();
		lastEmploye.setMatricule("C100000");
		employeRepository.save(lastEmploye);
		// When
		try {
			employeService.embaucheEmploye("Test", "Test", Poste.COMMERCIAL, NiveauEtude.BTS_IUT, 1.0);
		} catch (EmployeException e) {
			Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
		}

		// Then
		Assertions.assertEquals("100000", employeRepository.findLastMatricule());
	}

	@ParameterizedTest
	@MethodSource({ "employeTestingNullEtConditionStandard" })
	public void testEmployeServiceEmbaucheEmployeStandardEtNull(String nom, String prenom, Poste poste,
			NiveauEtude niveauEtude, Double tempsPartiel, String messageExceptionAttendu, Class<?> classException) {
		try {
			employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
		} catch (Exception e) {
			Assertions.assertEquals(messageExceptionAttendu, e.getMessage());
			Assertions.assertEquals(classException, e.getClass());
		}
	}

	/**
	 * @return jeu de test pour null condition et condition standard
	 */
	private static Stream<Arguments> employeTestingNullEtConditionStandard() {
		return Stream.of(
				Arguments.of(null, null, null, null, 1.0, "Impossible de créer un employé sans poste !",
						EmployeException.class),
				(Arguments.of("Test", "Test", Poste.TECHNICIEN, NiveauEtude.INGENIEUR, 1.0, "", null)),
				(Arguments.of("Test", "Test", Poste.TECHNICIEN, NiveauEtude.INGENIEUR, null, "", null)));
	}

}
