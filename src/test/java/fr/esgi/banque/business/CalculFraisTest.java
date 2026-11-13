package fr.esgi.banque.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculFraisTest {

	private static final int AGE_ADULTE = 30;
	private static final BigDecimal SOLDE_FAIBLE = new BigDecimal("500.00");

	private final CalculFrais calculFrais = new CalculFrais();

	@Nested
	@DisplayName("F1 : titulaire majeur et solde moyen obligatoire")
	class F1Validation {

		@ParameterizedTest(name = "âge {0}")
		@ValueSource(ints = {17, 0, -1})
		@DisplayName("Un titulaire de moins de 18 ans est refusé")
		void fraisMensuels_titulaireMineur_leveIllegalArgumentException(int age) {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> calculFrais.fraisMensuels(age, SOLDE_FAIBLE, false))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Un titulaire de 18 ans pile est accepté")
		void fraisMensuels_titulaireDe18Ans_neLevePasDException() {
			// Arrange / Act / Assert
			assertThatCode(() -> calculFrais.fraisMensuels(18, SOLDE_FAIBLE, false))
					.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Un solde moyen null est refusé")
		void fraisMensuels_soldeMoyenNull_leveIllegalArgumentException() {
			// Arrange / Act / Assert
			assertThatThrownBy(() -> calculFrais.fraisMensuels(AGE_ADULTE, null, false))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("F2 : frais de tenue de compte de 2,00 € par mois")
	class F2FraisDeTenue {

		@ParameterizedTest(name = "âge {0}, solde moyen {1}")
		@CsvSource({
				"27, 999.99",
				"30, 500.00",
				"65, 0"
		})
		@DisplayName("Sans exonération ni carte premium, les frais sont de 2,00 €")
		void fraisMensuels_sansExonerationNiPremium_renvoieFraisDeTenue(int age, String soldeMoyen) {
			// Act
			BigDecimal frais = calculFrais.fraisMensuels(age, new BigDecimal(soldeMoyen), false);

			// Assert
			assertThat(frais).isEqualByComparingTo("2.00");
		}
	}

	@Nested
	@DisplayName("F3 : tenue offerte aux titulaires de moins de 26 ans")
	class F3Jeune {

		@ParameterizedTest(name = "âge {0} -> frais {1}")
		@CsvSource({
				"18, 0",
				"25, 0",
				"26, 2.00",
				"27, 2.00"
		})
		@DisplayName("La tenue est offerte avant 26 ans, due à partir de 26 ans")
		void fraisMensuels_selonAge_tenueOfferteAvant26Ans(int age, String fraisAttendus) {
			// Act
			BigDecimal frais = calculFrais.fraisMensuels(age, SOLDE_FAIBLE, false);

			// Assert
			assertThat(frais).isEqualByComparingTo(fraisAttendus);
		}
	}

	@Nested
	@DisplayName("F4 : tenue offerte si le solde moyen atteint 1 000 € ou plus")
	class F4SoldeEleve {

		@ParameterizedTest(name = "solde moyen {0} -> frais {1}")
		@CsvSource({
				"999.99, 2.00",
				"1000, 0",
				"1000.00, 0",
				"1000.01, 0"
		})
		@DisplayName("La tenue est offerte dès 1 000 € de solde moyen")
		void fraisMensuels_selonSoldeMoyen_tenueOfferteDes1000(String soldeMoyen, String fraisAttendus) {
			// Act
			BigDecimal frais = calculFrais.fraisMensuels(AGE_ADULTE, new BigDecimal(soldeMoyen), false);

			// Assert
			assertThat(frais).isEqualByComparingTo(fraisAttendus);
		}
	}

}
