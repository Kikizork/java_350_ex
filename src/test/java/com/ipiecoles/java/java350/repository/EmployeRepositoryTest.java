package com.ipiecoles.java.java350.repository;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipiecoles.java.java350.model.Employe;

/**
 * @author frederic.enee Test unitaire query
 *         avgPerformanceWhereMatriculeStartsWith
 */

@SpringBootTest
public class EmployeRepositoryTest {

	@Autowired
	public EmployeRepository employeRepository;

	@ParameterizedTest
	@MethodSource("performanceEtMatricules")
	public void test(String matricule, Integer performance, Double average) {
		// Given
		Employe employe = new Employe();
		employe.setMatricule(matricule);
		employe.setPerformance(performance);
		// When
		employeRepository.save(employe);
		Double avg = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
		// Then
		Assertions.assertEquals(average, avg);
	}

	static Stream<Arguments> performanceEtMatricules() {

		return Stream.of(Arguments.of("C00001", 2, 2.0), Arguments.of("C00002", 3, 2.5),
				Arguments.of("C00003", 3, 8d / 3d), Arguments.of("C00004", 2, 2.5), Arguments.of("C00005", 1, 2.2),
				Arguments.of("C00006", 2, 13d / 6d), Arguments.of("C00007", 3, 16d / 7d));

	}
}
