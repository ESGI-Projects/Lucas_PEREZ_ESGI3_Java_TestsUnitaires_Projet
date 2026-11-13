package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.service.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirementServiceImplTest {

	private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
	/** Vendredi 13/11/2026 à 10h : un jour ouvré. */
	private static final Clock VENDREDI = Clock.fixed(
			ZonedDateTime.of(2026, 11, 13, 10, 0, 0, 0, PARIS).toInstant(), PARIS);

	private static final String NUMERO_SOURCE = "FR001";
	private static final String NUMERO_DESTINATION = "FR002";
	private static final BigDecimal MONTANT = new BigDecimal("50.00");

	@Mock
	private CompteRepository repository;

	@Mock
	private NotificationService notifications;

	private VirementServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new VirementServiceImpl(repository, notifications, VENDREDI);
	}

	/** Fabrique un vrai compte (objet métier simple, pas de mock), sans découvert autorisé. */
	private static Compte compte(String numero, String solde) {
		return new Compte(numero, "Titulaire " + numero, new BigDecimal(solde), BigDecimal.ZERO);
	}

	/** Bouchon : le repository trouve ce compte par son numéro. */
	private void stubCompte(Compte compte) {
		when(repository.trouver(compte.getNumero())).thenReturn(Optional.of(compte));
	}

	/** Bouchon : le repository ne trouve aucun compte pour ce numéro. */
	private void stubCompteInexistant(String numero) {
		when(repository.trouver(numero)).thenReturn(Optional.empty());
	}

	@Nested
	@DisplayName("V1 : un montant null, nul ou négatif est refusé")
	class V1Montant {

		@ParameterizedTest(name = "montant [{0}]")
		@NullSource
		@ValueSource(strings = {"0", "0.00", "-0.01"})
		@DisplayName("Un montant null, nul ou négatif lève IllegalArgumentException")
		void virer_montantInvalide_leveIllegalArgumentException(BigDecimal montant) {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> service.virer(NUMERO_SOURCE, NUMERO_DESTINATION, montant))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@ParameterizedTest(name = "montant [{0}]")
		@NullSource
		@ValueSource(strings = {"0", "-0.01"})
		@DisplayName("Un montant invalide est refusé avant tout accès aux comptes ou aux notifications")
		void virer_montantInvalide_aucuneInteraction(BigDecimal montant) {
			// Act
			catchThrowable(() -> service.virer(NUMERO_SOURCE, NUMERO_DESTINATION, montant));

			// Assert
			verifyNoInteractions(repository, notifications);
		}
	}

	@Nested
	@DisplayName("V2 : un virement vers le même compte est refusé")
	class V2MemeCompte {

		@Test
		@DisplayName("Source et destination identiques lèvent IllegalArgumentException")
		void virer_memeCompte_leveIllegalArgumentException() {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> service.virer(NUMERO_SOURCE, NUMERO_SOURCE, MONTANT))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("V3 : les deux comptes doivent exister")
	class V3CompteIntrouvable {

		@Test
		@DisplayName("Un compte source inexistant lève CompteIntrouvableException")
		void virer_sourceInexistante_leveCompteIntrouvableException() {
			// Arrange
			stubCompteInexistant(NUMERO_SOURCE);

			// Act / Assert
			assertThatThrownBy(() -> service.virer(NUMERO_SOURCE, NUMERO_DESTINATION, MONTANT))
					.isInstanceOf(CompteIntrouvableException.class);
		}

		@Test
		@DisplayName("Un compte destination inexistant lève CompteIntrouvableException")
		void virer_destinationInexistante_leveCompteIntrouvableException() {
			// Arrange
			stubCompte(compte(NUMERO_SOURCE, "100.00"));
			stubCompteInexistant(NUMERO_DESTINATION);

			// Act / Assert
			assertThatThrownBy(() -> service.virer(NUMERO_SOURCE, NUMERO_DESTINATION, MONTANT))
					.isInstanceOf(CompteIntrouvableException.class);
		}

		@Test
		@DisplayName("Quand un compte est introuvable, aucun compte n'est sauvegardé")
		void virer_destinationInexistante_aucuneSauvegarde() {
			// Arrange
			stubCompte(compte(NUMERO_SOURCE, "100.00"));
			stubCompteInexistant(NUMERO_DESTINATION);

			// Act
			catchThrowable(() -> service.virer(NUMERO_SOURCE, NUMERO_DESTINATION, MONTANT));

			// Assert
			verify(repository, never()).sauvegarder(any());
		}
	}

}
