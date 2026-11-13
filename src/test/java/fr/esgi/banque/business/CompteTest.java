package fr.esgi.banque.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {

	private static final String NUMERO = "FR001";
	private static final String TITULAIRE = "Alice";
	private static final BigDecimal SOLDE = new BigDecimal("100.00");
	private static final BigDecimal DECOUVERT = new BigDecimal("200");

	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}

	@Nested
	@DisplayName("C1 : numéro et titulaire obligatoires")
	class C1NumeroEtTitulaire {

		@ParameterizedTest(name = "numéro [{0}]")
		@NullAndEmptySource
		@ValueSource(strings = {" ", "   "})
		@DisplayName("Un numéro null, vide ou blanc est refusé")
		void constructeur_numeroAbsent_leveIllegalArgumentException(String numero) {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> new Compte(numero, TITULAIRE, SOLDE, DECOUVERT))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@ParameterizedTest(name = "titulaire [{0}]")
		@NullAndEmptySource
		@ValueSource(strings = {" ", "   "})
		@DisplayName("Un titulaire null, vide ou blanc est refusé")
		void constructeur_titulaireAbsent_leveIllegalArgumentException(String titulaire) {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> new Compte(NUMERO, titulaire, SOLDE, DECOUVERT))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Un numéro et un titulaire renseignés créent le compte")
		void constructeur_numeroEtTitulaireValides_creeLeCompte() {
			// Act
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Assert
			assertThat(compte.getNumero()).isEqualTo(NUMERO);
			assertThat(compte.getTitulaire()).isEqualTo(TITULAIRE);
		}
	}

	@Nested
	@DisplayName("C2 : solde initial et découvert obligatoires, découvert positif ou nul")
	class C2SoldeEtDecouvert {

		@Test
		@DisplayName("Un solde initial null est refusé")
		void constructeur_soldeNull_leveIllegalArgumentException() {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> new Compte(NUMERO, TITULAIRE, null, DECOUVERT))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Un découvert autorisé null est refusé")
		void constructeur_decouvertNull_leveIllegalArgumentException() {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> new Compte(NUMERO, TITULAIRE, SOLDE, null))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Un découvert autorisé négatif (-0,01) est refusé")
		void constructeur_decouvertNegatif_leveIllegalArgumentException() {
			// Arrange
			BigDecimal decouvertNegatif = new BigDecimal("-0.01");

			// Act / Assert
			assertThatThrownBy(() -> new Compte(NUMERO, TITULAIRE, SOLDE, decouvertNegatif))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@ParameterizedTest(name = "découvert {0}")
		@ValueSource(strings = {"0", "0.00", "0.01"})
		@DisplayName("Un découvert autorisé nul ou positif est accepté")
		void constructeur_decouvertPositifOuNul_creeLeCompte(String decouvert) {
			// Act
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, new BigDecimal(decouvert));

			// Assert
			assertThat(compte.getDecouvertAutorise()).isEqualByComparingTo(decouvert);
		}
	}

	@Nested
	@DisplayName("C3 : on ne crédite qu'un montant strictement positif")
	class C3Crediter {

		@Test
		@DisplayName("Créditer un montant null est refusé")
		void crediter_montantNull_leveIllegalArgumentException() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act / Assert
			assertThatThrownBy(() -> compte.crediter(null))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@ParameterizedTest(name = "montant {0}")
		@ValueSource(strings = {"0", "0.00", "-0.01"})
		@DisplayName("Créditer un montant nul ou négatif est refusé")
		void crediter_montantNulOuNegatif_leveIllegalArgumentException(String montant) {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act / Assert
			assertThatThrownBy(() -> compte.crediter(new BigDecimal(montant)))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Créditer le plus petit montant positif (0,01) augmente le solde")
		void crediter_montantMinimal_augmenteLeSolde() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act
			compte.crediter(new BigDecimal("0.01"));

			// Assert
			assertThat(compte.getSolde()).isEqualByComparingTo("100.01");
		}
	}

	@Nested
	@DisplayName("C4 : on ne débite qu'un montant strictement positif")
	class C4Debiter {

		@Test
		@DisplayName("Débiter un montant null est refusé")
		void debiter_montantNull_leveIllegalArgumentException() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act / Assert
			assertThatThrownBy(() -> compte.debiter(null))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@ParameterizedTest(name = "montant {0}")
		@ValueSource(strings = {"0", "0.00", "-0.01"})
		@DisplayName("Débiter un montant nul ou négatif est refusé")
		void debiter_montantNulOuNegatif_leveIllegalArgumentException(String montant) {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act / Assert
			assertThatThrownBy(() -> compte.debiter(new BigDecimal(montant)))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Débiter un montant positif diminue le solde")
		void debiter_montantPositif_diminueLeSolde() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act
			compte.debiter(new BigDecimal("30.00"));

			// Assert
			assertThat(compte.getSolde()).isEqualByComparingTo("70.00");
		}
	}

	@Nested
	@DisplayName("C5 : débit autorisé jusqu'à -découvert inclus")
	class C5LimiteDecouvert {

		@ParameterizedTest(name = "débit {0} -> solde {1}")
		@CsvSource({
				"299.99, -199.99",
				"300.00, -200.00"
		})
		@DisplayName("Solde 100, découvert 200 : un débit jusqu'à la limite exacte est accepté")
		void debiter_jusquALaLimiteDuDecouvert_estAccepte(String montant, String soldeAttendu) {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act
			compte.debiter(new BigDecimal(montant));

			// Assert
			assertThat(compte.getSolde()).isEqualByComparingTo(soldeAttendu);
		}

		@Test
		@DisplayName("Solde 100, découvert 200 : débiter 300,01 lève SoldeInsuffisantException")
		void debiter_auDelaDuDecouvert_leveSoldeInsuffisantException() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act / Assert
			assertThatThrownBy(() -> compte.debiter(new BigDecimal("300.01")))
					.isInstanceOf(SoldeInsuffisantException.class);
		}

		@Test
		@DisplayName("Un débit refusé pour solde insuffisant ne modifie pas le solde")
		void debiter_auDelaDuDecouvert_soldeInchange() {
			// Arrange
			Compte compte = new Compte(NUMERO, TITULAIRE, SOLDE, DECOUVERT);

			// Act
			catchThrowable(() -> compte.debiter(new BigDecimal("300.01")));

			// Assert
			assertThat(compte.getSolde()).isEqualByComparingTo("100.00");
		}
	}

}
