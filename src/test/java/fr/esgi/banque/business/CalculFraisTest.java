package fr.esgi.banque.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

}
