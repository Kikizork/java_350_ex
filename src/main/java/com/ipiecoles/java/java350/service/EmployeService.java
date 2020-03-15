package com.ipiecoles.java.java350.service;

import java.time.LocalDate;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;

@Service
public class EmployeService {

	@Autowired
	private EmployeRepository employeRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeService.class);

	/**
	 * Méthode enregistrant un nouvel employé dans l'entreprise
	 *
	 * @param nom          Le nom de l'employé
	 * @param prenom       Le prénom de l'employé
	 * @param poste        Le poste de l'employé
	 * @param niveauEtude  Le niveau d'étude de l'employé
	 * @param tempsPartiel Le pourcentage d'activité en cas de temps partiel
	 *
	 * @throws EmployeException      Si on arrive au bout des matricules possibles
	 * @throws EntityExistsException Si le matricule correspond à un employé
	 *                               existant
	 */
	public void embaucheEmploye(String nom, String prenom, Poste poste, NiveauEtude niveauEtude, Double tempsPartiel)
			throws EmployeException {

		if (poste == null) {
			LOGGER.error("Erreur, paramètre poste null");
			throw new EmployeException("Impossible de créer un employé sans poste !");
		}
		// Récupération du type d'employé à partir du poste
		String typeEmploye = poste.name().substring(0, 1);

		// Récupération du dernier matricule...
		String lastMatricule = employeRepository.findLastMatricule();
		if (lastMatricule == null) {
			lastMatricule = Entreprise.MATRICULE_INITIAL;
		}
		// ... et incrémentation
		Integer numeroMatricule = Integer.parseInt(lastMatricule) + 1;
		if (numeroMatricule >= 90000) {
			LOGGER.warn("Le numéro de matricule est de {} limite des 100000 bientôt atteinte", numeroMatricule);
		}
		if (numeroMatricule >= 100000) {
			LOGGER.error("Erreur, limite des 100000 matricules atteinte !");
			throw new EmployeException("Limite des 100000 matricules atteinte !");
		}
		// On complète le numéro avec des 0 à gauche
		String matricule = "00000" + numeroMatricule;
		matricule = typeEmploye + matricule.substring(matricule.length() - 5);

		// Calcul du salaire
		Double salaire = Entreprise.getCoeffSalaireEtudes().get(niveauEtude) * Entreprise.SALAIRE_BASE;
		if (tempsPartiel != null) {
			salaire = salaire * tempsPartiel;
		}
		salaire = Math.round(salaire * 100d) / 100d;

		// Création et sauvegarde en BDD de l'employé.
		Employe employe = new Employe(nom, prenom, matricule, LocalDate.now(), salaire, Entreprise.PERFORMANCE_BASE,
				tempsPartiel);

		employeRepository.save(employe);

	}

	/**
	 * Méthode calculant la performance d'un commercial en fonction de ses objectifs
	 * et du chiffre d'affaire traité dans l'année. Cette performance lui est
	 * affectée et sauvegardée en BDD
	 *
	 * 1 : Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé,
	 * le commercial retombe à la performance de base 2 : Si le chiffre d'affaire
	 * est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de
	 * performance (dans la limite de la performance de base) 3 : Si le chiffre
	 * d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la
	 * même. 4 : Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de
	 * performance 5 : Si le chiffre d'affaire est supérieur de plus de 20%, il
	 * gagne 4 de performance
	 *
	 * Si la performance ainsi calculée est supérieure à la moyenne des performances
	 * des commerciaux, il reçoit + 1 de performance.
	 *
	 * @param matricule  le matricule du commercial
	 * @param caTraite   le chiffre d'affaire traité par le commercial pendant
	 *                   l'année
	 * @param objectifCa l'object de chiffre d'affaire qui lui a été fixé
	 *
	 * @throws EmployeException Si le matricule est null ou ne commence pas par un C
	 */
	public void calculPerformanceCommercial(String matricule, Long caTraite, Long objectifCa) throws EmployeException {
		// Vérification des paramètres d'entrée
		if (caTraite == null || caTraite < 0) {
			LOGGER.error("Paramètre caTraite null ou négatif");
			throw new EmployeException("Le chiffre d'affaire traité ne peut être négatif ou null !");
		}
		if (objectifCa == null || objectifCa < 0) {
			LOGGER.error("Paramètre objectifCa null ou négatif");
			throw new EmployeException("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
		}
		if (matricule == null || !matricule.startsWith("C")) {
			LOGGER.error("Paramètre matricule null ou non commercial");
			throw new EmployeException("Le matricule ne peut être null et doit commencer par un C !");
		}
		// Recherche de l'employé dans la base
		Employe employe = employeRepository.findByMatricule(matricule);
		if (employe == null) {
			LOGGER.error("Pas d'employe avec le matricule {} existant en base de donnée", matricule);
			throw new EmployeException("Le matricule " + matricule + " n'existe pas !");
		}

		Integer performance = calculPerformance(caTraite, objectifCa, employe);

		// Calcul de la performance moyenne
		Double performanceMoyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
		if (performanceMoyenne != null && performance > performanceMoyenne) {
			performance++;
		}

		// Affectation et sauvegarde
		employe.setPerformance(performance);
		employeRepository.save(employe);
		LOGGER.info("Succès exécution");
	}

	/**
	 * Calcul de la performance en fonction du % de chiffre d'affaire par rapport à
	 * l'objectif de chiffre d'affaire fixé.
	 * 
	 * @param caTraite
	 * @param objectifCa
	 * @param employe
	 * @return Integer performance
	 */
	private Integer calculPerformance(Long caTraite, Long objectifCa, Employe employe) {

		// Cas 2
		if (caTraite >= objectifCa * 0.8 && caTraite < objectifCa * 0.95) {
			return Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance() - 2);
		}
		// Cas 3
		else if (caTraite >= objectifCa * 0.95 && caTraite <= objectifCa * 1.05) {
			return Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance());
		}
		// Cas 4
		else if (caTraite <= objectifCa * 1.2 && caTraite > objectifCa * 1.05) {
			return employe.getPerformance() + 1;
		}
		// Cas 5
		else if (caTraite > objectifCa * 1.2) {
			return employe.getPerformance() + 4;
		} // Si autre cas, on reste à la performance de base
		else {
			return Entreprise.PERFORMANCE_BASE;
		}
	}
}
