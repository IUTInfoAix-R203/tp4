package fr.univ_amu.iut.exercice2;

/**
 * Exercice 2 - Replace Magic Number with Symbolic Constant.
 *
 * <p>Cette classe calcule le prix final d'une commande en appliquant trois règles métier : TVA,
 * remise fidélité au-delà d'un seuil, et frais de port en dessous d'un autre seuil. Elle
 * fonctionne, les tests de caractérisation la valident - mais le code est <b>truffé de nombres
 * magiques</b>. Un·e lecteur·ice doit deviner ce que représentent {@code 1.20}, {@code 500.0},
 * {@code 0.95}, {@code 50.0}, {@code 8.0}.
 *
 * <p>Refactoring attendu :
 *
 * <ul>
 *   <li><b>Replace Magic Number with Symbolic Constant</b> : extraire les cinq constantes métier
 *       comme {@code private static final double} avec des noms parlants. Après le refactoring, la
 *       ligne qui applique la TVA doit se lire comme "multiplier par TAUX_TVA" et non "multiplier
 *       par 1.20".
 * </ul>
 *
 * <p>Constantes à extraire (noms recommandés par les tests de structure) :
 *
 * <table>
 *   <tr><th>Valeur</th><th>Nom</th><th>Sens métier</th></tr>
 *   <tr><td>1.20</td><td>TAUX_TVA</td><td>TVA à 20 %</td></tr>
 *   <tr><td>500.0</td><td>SEUIL_REMISE_FIDELITE</td><td>Seuil au-delà duquel la remise s'applique</td></tr>
 *   <tr><td>0.95</td><td>TAUX_REMISE_FIDELITE</td><td>Remise fidèle : 5 % = multiplier par 0.95</td></tr>
 *   <tr><td>50.0</td><td>SEUIL_FRAIS_PORT_OFFERT</td><td>Seuil au-dessus duquel le port est offert</td></tr>
 *   <tr><td>8.0</td><td>MONTANT_FRAIS_PORT</td><td>Frais de port pour les petites commandes</td></tr>
 * </table>
 *
 * <p>Ce refactoring n'extrait pas de méthode (voir exercice 1 pour Extract Method). Il se concentre
 * uniquement sur les constantes nommées : un pas à la fois.
 */
public class CalculPrix {

  /**
   * Calcule le prix final d'une commande.
   *
   * @param montantHT montant hors taxe de la commande
   * @param clientFidele true si le·la client·e a le statut fidèle
   * @return montant final à payer (TTC, remise éventuelle, frais de port inclus)
   */
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
