package fr.univ_amu.iut.exercice2;

/// Exercice 2 - Replace Magic Number with Symbolic Constant.
///
/// Cette classe calcule le prix final d'une commande en appliquant trois règles métier : TVA,
/// remise fidélité au-delà d'un seuil, et frais de port en dessous d'un autre seuil. Elle
/// fonctionne, les tests de caractérisation la valident - mais le code est **truffé de nombres
/// magiques**. Un·e lecteur·ice doit deviner ce que représentent `1.20`, `500.0`, `0.95`, `50.0`,
/// `8.0`.
///
/// Refactoring attendu :
///
/// - **Replace Magic Number with Symbolic Constant** : extraire les cinq constantes métier comme
///   `private static final double` avec des noms parlants. Après le refactoring, la ligne qui
///   applique la TVA doit se lire comme "multiplier par TAUX_TVA" et non "multiplier par 1.20".
///
/// Constantes à extraire (noms recommandés par les tests de structure) :
///
/// | Valeur | Nom                      | Sens métier                                            |
/// |--------|--------------------------|--------------------------------------------------------|
/// | 1.20   | TAUX_TVA                 | TVA à 20 %                                             |
/// | 500.0  | SEUIL_REMISE_FIDELITE    | Seuil au-delà duquel la remise s'applique              |
/// | 0.95   | TAUX_REMISE_FIDELITE     | Remise fidèle : 5 % = multiplier par 0.95              |
/// | 50.0   | SEUIL_FRAIS_PORT_OFFERT  | Seuil au-dessus duquel le port est offert              |
/// | 8.0    | MONTANT_FRAIS_PORT       | Frais de port pour les petites commandes               |
///
/// Ce refactoring n'extrait pas de méthode (voir exercice 1 pour Extract Method). Il se concentre
/// uniquement sur les constantes nommées : un pas à la fois.
public class CalculPrix {

  /// Calcule le prix final d'une commande.
  ///
  /// @param montantHT montant hors taxe de la commande
  /// @param clientFidele true si le·la client·e a le statut fidèle
  /// @return montant final à payer (TTC, remise éventuelle, frais de port inclus)
  public double calculerPrixFinal(double montantHT, boolean clientFidele) {
    // TVA
    double montantTTC = montantHT * 1.20;
    // Remise fidélité au-delà du seuil
    if (clientFidele && montantTTC > 500.0) {
      montantTTC = montantTTC * 0.95;
    }
    // Frais de port en dessous du seuil
    double fraisPort = 0;
    if (montantTTC < 50.0) {
      fraisPort = 8.0;
    }
    return montantTTC + fraisPort;
  }
}
