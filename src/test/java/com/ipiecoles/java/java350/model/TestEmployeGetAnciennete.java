package com.ipiecoles.java.java350.model;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.ipiecoles.java.java350.model.Employe;

public class TestEmployeGetAnciennete {

	@Test
	public void testGetAnneeAnciennete2Year() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now().minusYears(2));
		
		// When
		Integer nbAnnees = employe.getNombreAnneeAnciennete();
		
		//Then 
		Assertions.assertThat(nbAnnees).isEqualTo(2);
	
	}
	
	@Test
	public void testGetAnneeAncienneteless2year() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now().plusYears(2));
		
		// When
		Integer nbAnnees = employe.getNombreAnneeAnciennete();
		
		//Then 
		Assertions.assertThat(nbAnnees).isEqualTo(0);
	
	}
	
	@Test
	public void testGetAnneeAncienneteToday() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		
		// When
		Integer nbAnnees = employe.getNombreAnneeAnciennete();
		
		//Then 
		Assertions.assertThat(nbAnnees).isEqualTo(0);
	
	}
	
	@Test
	public void testGetAnneeAncienneteIsNull() {
		
		//Given
		Employe employe = new Employe();
		
		
		// When
		Integer nbAnnees = employe.getNombreAnneeAnciennete();
		
		//Then 
		Assertions.assertThat(nbAnnees).isEqualTo(0);
	
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualThree() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now().minusYears(3));
		employe.setMatricule("00000");
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(1300d);
		
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualZero() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		employe.setMatricule("00000");
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(1000d);
		
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualZeroAndNoMatricule() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(1000d);
		
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualZeroAndNoMatriculeAndPerformanceEqual2() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		employe.setPerformance(2);
		
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(2300d);
		
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualZeroAndIsAManager() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		employe.setPerformance(2);
		employe.setMatricule("M000000");
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(1700d);
		
	}
	
	@Test
	public void testGetPrimeAnnuelleAncienneteEqualZeroAndIsNotAManager() {
		
		//Given
		Employe employe = new Employe();
		employe.setDateEmbauche(LocalDate.now());
		employe.setPerformance(2);
		employe.setMatricule("C000000");
		
		//When
		Double primeAnnuelle = employe.getPrimeAnnuelle();
		
		//Then 
		Assertions.assertThat(primeAnnuelle).isEqualTo(2300d);
		
	}
}
