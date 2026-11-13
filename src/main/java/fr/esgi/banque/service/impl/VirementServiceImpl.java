package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.service.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import fr.esgi.banque.service.Virement;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

public class VirementServiceImpl {

	private final CompteRepository repository;
	private final NotificationService notifications;
	private final Clock horloge;

	public VirementServiceImpl(CompteRepository repository, NotificationService notifications, Clock horloge) {
		this.repository = repository;
		this.notifications = notifications;
		this.horloge = horloge;
	}

	public Virement virer(String numeroSource, String numeroDestination, BigDecimal montant) {
		verifierMontantStrictementPositif(montant);
		verifierComptesDifferents(numeroSource, numeroDestination);
		Compte source = trouverCompte(numeroSource);
		Compte destination = trouverCompte(numeroDestination);
		executer(source, destination, montant);
		return new Virement(numeroSource, numeroDestination, montant, LocalDate.now(horloge));
	}

	/** Débite d'abord (peut lever SoldeInsuffisantException), puis crédite et sauvegarde les deux comptes. */
	private void executer(Compte source, Compte destination, BigDecimal montant) {
		source.debiter(montant);
		destination.crediter(montant);
		repository.sauvegarder(source);
		repository.sauvegarder(destination);
	}

	private Compte trouverCompte(String numero) {
		return repository.trouver(numero)
				.orElseThrow(() -> new CompteIntrouvableException(numero));
	}

	private static void verifierComptesDifferents(String numeroSource, String numeroDestination) {
		if (numeroSource.equals(numeroDestination)) {
			throw new IllegalArgumentException("Un virement vers le même compte est interdit");
		}
	}

	private static void verifierMontantStrictementPositif(BigDecimal montant) {
		if (montant == null || montant.signum() <= 0) {
			throw new IllegalArgumentException("Le montant du virement doit être strictement positif");
		}
	}

}
