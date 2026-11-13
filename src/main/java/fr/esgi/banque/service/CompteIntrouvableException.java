package fr.esgi.banque.service;

public class CompteIntrouvableException extends RuntimeException {

	public CompteIntrouvableException(String numeroCompte) {
		super("Compte introuvable : " + numeroCompte);
	}

}
