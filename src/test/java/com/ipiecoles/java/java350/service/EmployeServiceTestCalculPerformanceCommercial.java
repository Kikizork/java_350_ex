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
import com.ipiecoles.java.java350.repository.EmployeRepository;

@SpringBootTest
public class EmployeServiceTestCalculPerformanceCommercial {

	@Autowired
	public EmployeRepository employeRepository;

	@Autowired
	public EmployeService employeService;

	@BeforeEach
	public void remiseAZero() {
		employeRepository.deleteAll();
	}

	@Test
	public void testCalculPerformanceCommercialSansEmploye() {
		// Given
		String matricule = "C99999";
		Long caTraite = 10000L;
		Long objectifCa = 10000L;
		String exception = "Le matricule C99999 n'existe pas !";
		Integer performanceAttendue = 0;

		// When
		try {
			employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
			// Then
		} catch (EmployeException e) {
			Assertions.assertEquals(exception, e.getMessage());
		}

	}

	@ParameterizedTest
	@MethodSource({ "commercialTestingCalculPerformanceCommercialTestNullCondition",
			"commercialTestingCalculPerformanceCommercialTestConditionNormales" })
	public void testCalculPerformanceCommercial(String matricule, Long caTraite, Long objectifCa, String exception,
			Integer performanceAttendue) {

		// Given
		Employe employe = new Employe();
		employe.setMatricule(matricule);
		employeRepository.save(employe);
		// When
		try {
			employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

			// Then
			Employe employeApresCalculPerformanceCommercial = employeRepository.findByMatricule(matricule);
			Assertions.assertEquals(performanceAttendue, employeApresCalculPerformanceCommercial.getPerformance());

		} catch (EmployeException e) {
			Assertions.assertEquals(exception, e.getMessage());
		}

	}

	private static Stream<Arguments> commercialTestingCalculPerformanceCommercialTestNullCondition() {
		return Stream.of(
				Arguments.of(null, null, null, "Le chiffre d'affaire traité ne peut être négatif ou null !", null),
				Arguments.of(null, -157L, null, "Le chiffre d'affaire traité ne peut être négatif ou null !", null),
				Arguments.of(null, 157L, null, "L'objectif de chiffre d'affaire ne peut être négatif ou null !", null),
				Arguments.of(null, 157L, -145L, "L'objectif de chiffre d'affaire ne peut être négatif ou null !", null),
				Arguments.of(null, 157L, 145L, "Le matricule ne peut être null et doit commencer par un C !", null),
				Arguments.of("M00001", 157L, 145L, "Le matricule ne peut être null et doit commencer par un C !",
						null));
	}

	private static Stream<Arguments> commercialTestingCalculPerformanceCommercialTestConditionNormales() {
		return Stream.of(Arguments.of("C00001", 7500L, 10000L, "", 1), Arguments.of("C00001", 9000L, 10000L, "", 1),
				Arguments.of("C00002", 9500L, 10000L, "", 1), Arguments.of("C00003", 11000L, 10000L, "", 3),
				Arguments.of("C00004", 13000L, 10000L, "", 6));
	}

}
