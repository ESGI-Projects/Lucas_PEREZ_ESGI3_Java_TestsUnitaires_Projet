package fr.esgi.banque.service.impl;

import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import fr.esgi.banque.service.Virement;

import java.math.BigDecimal;
import java.time.Clock;

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
		if (montant == null || montant.signum() <= 0) {
			throw new IllegalArgumentException("Le montant du virement doit être strictement positif");
		}
		return null;
	}

}
