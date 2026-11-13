package fr.esgi.banque.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

}
