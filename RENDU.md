# Rendu — Tests unitaires Java (banque)

- **Nom** : PEREZ
- **Prénom** : Lucas
- **Campus** : ESGI Aix – Campus Skolae

## Partie A — défauts trouvés

Chaque défaut est prouvé par un commit `test:` (rouge) suivi immédiatement du commit `fix:`.

### Classe `Compte`

| Règle | Défaut constaté | Correction | Test qui le prouve |
|---|---|---|---|
| C3 | `crediter` testait `signum() < 0` : un crédit de `0` (ou `0.00`) était accepté alors que le montant doit être strictement positif. | `signum() <= 0` | `CompteTest.C3Crediter#crediter_montantNulOuNegatif_leveIllegalArgumentException` (cas `0` et `0.00`) |
| C5 | `debiter` refusait un débit qui amène le solde **exactement** à `-découvertAutorisé` (`compareTo(...) <= 0`). Ex. : solde 100, découvert 200, débit 300 → `SoldeInsuffisantException`. | Comparaison stricte `compareTo(...) < 0` | `CompteTest.C5LimiteDecouvert#debiter_jusquALaLimiteDuDecouvert_estAccepte` (cas 300.00 → -200.00) |
| C6 | `estVide` utilisait `solde.equals(BigDecimal.ZERO)`, qui compare aussi l'échelle : un solde `0.00` n'était pas considéré comme vide. | `solde.signum() == 0` | `CompteTest.C6EstVide#estVide_soldeNulQuelleQueSoitLEchelle_renvoieVrai` (cas `0.00` et `0.000`) |

Règles C1, C2, C4 et C7 : conformes, couvertes par des tests (`CompteTest`).

## Partie B — service de virement (TDD)

_En cours._

## Couverture de code

_Capture IntelliJ (Run with Coverage) à venir._
