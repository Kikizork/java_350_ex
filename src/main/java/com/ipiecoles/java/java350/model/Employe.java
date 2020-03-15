package com.ipiecoles.java.java350.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ipiecoles.java.java350.exception.EmployeException;

/**
 * Class employe.
 *
 */
@Entity
public class Employe {

	private static final Logger LOGGER = LoggerFactory.getLogger(Employe.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String nom;

	private String prenom;

	private String matricule;

	private LocalDate dateEmbauche;

	private Double salaire = Entreprise.SALAIRE_BASE;

	private Integer performance = Entreprise.PERFORMANCE_BASE;

	private Double tempsPartiel = 1.0;

	public Employe() {
	}

	public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire,
			Integer performance, Double tempsPartiel) {
		this.nom = nom;
		this.prenom = prenom;
		this.matricule = matricule;
		this.dateEmbauche = dateEmbauche;
		this.salaire = salaire;
		this.performance = performance;
		this.tempsPartiel = tempsPartiel;
	}

	/**
	 * @return Nombre d'année d'ancienneté.
	 * @limits Retourne 0 si dateEmbauche null ou future
	 */
	public Integer getNombreAnneeAnciennete() {
		if (dateEmbauche == null) {
			return 0;
		} else if (LocalDate.now().getYear() - dateEmbauche.getYear() > 0) {
			return LocalDate.now().getYear() - dateEmbauche.getYear();
		} else {
			return 0;
		}
	}

	/**
	 * @return nombre de conges totaux
	 */
	public Integer getNbConges() {
		return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
	}

	/**
	 * Execute la fonction getNombreDeRttPourUneAnnee pour l'année en cours
	 */
	public Integer getNombreDeRttAnneeEnCours() {
		return getNombreDeRttPourUneAnnee(LocalDate.now());
	}

	/**
	 * La methode getNombreDeRttPourUneAnnee prends en paramètre une localDate et
	 * retourne le nombre de RTT pour l'année donnée et l'employé. La methode prends
	 * en compte les années bisextiles , le nombre de jours fériés hors week-end, le
	 * nombre de jours travaillés dans l'entreprise, le nombre de jours de week end
	 * dans l'année, le nombre de jours de congés avec l'ancienneté et le taux de
	 * temps partiel
	 * 
	 * @param localDate
	 * @return int nombreDeRtt
	 * @throws NullPointerException si param localDate null
	 */
	public Integer getNombreDeRttPourUneAnnee(LocalDate localDate) {

		// Teste la condition null retourne une erreur
		if (localDate == null) {
			LOGGER.error("Erreur fonction {}, paramètre localDate ne peut être null",
					Object.class.getEnclosingMethod());
			throw new NullPointerException("La date ne peut être nulle");
		}
		// Initialization du nombre de jours dans l'année et du nombre de jours de week
		// end de base
		// 52 - 2 = 104
		Integer nombreDeJoursDansLAnnee = localDate.isLeapYear() ? 366 : 365;
		Integer nombreDeJoursDeWeekEndDansLAnnee = 104;
		/*
		 * Une année posséde 52 semaines => 52 * 7 = 364 jours. + un jour bonus en année
		 * normale et 2 jours en année bissextile Ces jours bonus sont toujours
		 * successifs au jour de la semaine du premier jour de l'année. ce switch ajoute
		 * le bon montant de jours de week end suivant quel est le premier jour de
		 * l'année
		 */
		switch (LocalDate.of(localDate.getYear(), 1, 1).getDayOfWeek()) {
		case THURSDAY:
			if (localDate.isLeapYear()) {
				nombreDeJoursDeWeekEndDansLAnnee++;
			}
			break;
		case FRIDAY:
			if (localDate.isLeapYear()) {
				nombreDeJoursDeWeekEndDansLAnnee = nombreDeJoursDeWeekEndDansLAnnee + 2;
			} else {
				nombreDeJoursDeWeekEndDansLAnnee++;
			}
			break;
		case SATURDAY:
			nombreDeJoursDeWeekEndDansLAnnee++;
			break;
		default:
			break;
		}

		// Récupération du nombre de jours fériés tombant en semaine grâce à la fonction
		// joursFeries
		Integer nombreJourFeriesTombantEnSemaine = (int) Entreprise.joursFeries(localDate).stream()
				.filter(date -> date.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();

		// Calcul du nombreDeJoursRtt
		Integer nombreJoursRtt = (int) Math
				.ceil((nombreDeJoursDansLAnnee - getNbConges() - nombreDeJoursDeWeekEndDansLAnnee
						- nombreJourFeriesTombantEnSemaine - Entreprise.NB_JOURS_MAX_FORFAIT) * tempsPartiel);

		// Elimination du cas illogique des Rtt négatif possible si l'ancienneté est
		// importante
		return nombreJoursRtt > 0 ? nombreJoursRtt : 0;
	}

	/**
	 * Calcul de la prime annuelle selon la règle : Pour les managers : Prime
	 * annuelle de base bonnifiée par l'indice prime manager Pour les autres
	 * employés, la prime de base plus éventuellement la prime de performance
	 * calculée si l'employé n'a pas la performance de base, en multipliant la prime
	 * de base par un l'indice de performance (égal à la performance à laquelle on
	 * ajoute l'indice de prime de base)
	 *
	 * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en
	 * multipliant le nombre d'année d'ancienneté avec la prime d'ancienneté. La
	 * prime est calculée au pro rata du temps de travail de l'employé
	 *
	 * @return la prime annuelle de l'employé en Euros et cents
	 */
	public Double getPrimeAnnuelle() {
		// Calcul de la prime d'ancienneté
		Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
		Double prime;
		// Prime du manager (matricule commençant par M) : Prime annuelle de base
		// multipliée par l'indice prime manager
		// plus la prime d'anciennté.
		if (matricule != null && matricule.startsWith("M")) {
			prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
		}
		// Pour les autres employés en performance de base, uniquement la prime annuelle
		// plus la prime d'ancienneté.
		else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals((this.performance))) {
			prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
		}
		// Pour les employés plus performance, on bonnifie la prime de base en
		// multipliant par la performance de l'employé
		// et l'indice de prime de base.
		else {
			prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE)
					+ primeAnciennete;
		}
		// Au pro rata du temps partiel.
		return prime * this.tempsPartiel;
	}

	/**
	 * La fonction augmenterSalaire prend en paramètre un pourcentage et augmente le
	 * salaire de l'employé d'autant. La fonction throw une erreur quand le salaire
	 * ou le pourcentage est null, quand le salaire est négatif car cela n'a aucun
	 * sens et quand le pourcentage est négatif, car la fonction se nomme
	 * augmenterSalaire pas modifierSalaire !
	 * 
	 * @param pourcentage
	 * @throws NullPointerException
	 * @throws EmployeException
	 * 
	 */
	public void augmenterSalaire(Double pourcentage) throws EmployeException {

		if (this.salaire == null) {
			LOGGER.error("Erreur, le salaire de l'employe est null il ne peut être augmenté.");
			throw new NullPointerException("Erreur, le salaire de l'employe est null");
		} else if (this.salaire < 0.0) {
			LOGGER.error("Erreur, le salaire de l'employe ne peut être négatif");
			throw new EmployeException("Erreur, le salaire de l'employe est négatif");
		} else if (pourcentage == null) {
			LOGGER.error("Erreur, le pourcentage d'augmentation ne peut être null");
			throw new NullPointerException("Erreur, le paramètre pourcentage est null");
		} else if (pourcentage < 0D) {
			LOGGER.error("Erreur, le pourcentage d'augmentation ne peut être inférieur à zéro");
			throw new EmployeException("Erreur, le pourcentage d'augmentation est inférieur à zéro");
		} else {
			this.salaire = (this.salaire * ((100 + pourcentage) / 100));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * @return the matricule
	 */
	public String getMatricule() {
		return matricule;
	}

	/**
	 * @param matricule the matricule to set
	 */
	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	/**
	 * @return the dateEmbauche
	 */
	public LocalDate getDateEmbauche() {
		return dateEmbauche;
	}

	/**
	 * @param dateEmbauche the dateEmbauche to set
	 */
	public void setDateEmbauche(LocalDate dateEmbauche) {
		this.dateEmbauche = dateEmbauche;
	}

	/**
	 * @return the salaire
	 */
	public Double getSalaire() {
		return salaire;
	}

	/**
	 * @param salaire the salaire to set
	 */
	public void setSalaire(Double salaire) {
		this.salaire = salaire;
	}

	public Integer getPerformance() {
		return performance;
	}

	public void setPerformance(Integer performance) {
		if (performance == null || performance <= 0) {
			this.performance = 1;
		} else {
			this.performance = performance;
		}
	}

	public Double getTempsPartiel() {
		return tempsPartiel;
	}

	public void setTempsPartiel(Double tempsPartiel) {
		if (tempsPartiel == null || tempsPartiel <= 0) {
			this.tempsPartiel = 1d;
		} else {
			this.tempsPartiel = tempsPartiel;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Employe))
			return false;
		Employe employe = (Employe) o;
		return Objects.equals(id, employe.id) && Objects.equals(nom, employe.nom)
				&& Objects.equals(prenom, employe.prenom) && Objects.equals(matricule, employe.matricule)
				&& Objects.equals(dateEmbauche, employe.dateEmbauche) && Objects.equals(salaire, employe.salaire)
				&& Objects.equals(performance, employe.performance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nom, prenom, matricule, dateEmbauche, salaire, performance);
	}
}
