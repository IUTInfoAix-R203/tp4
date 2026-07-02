package fr.univ_amu.iut.exercice1;

/// Exercice 1 - Long Method : Extract Method.
///
/// Cette classe fonctionne : les tests de caractérisation la valident. Mais [#calculerTotal] est
/// un **Long Method** : elle fait trois choses à la suite (calcul HT, application TVA,
/// application remise). On veut que chacune devienne une méthode privée avec un nom qui dit ce
/// qu'elle fait, et que `calculerTotal` se lise comme un résumé de haut niveau.
///
/// Les constantes métier (`TAUX_TVA`, `SEUIL_REMISE`, `TAUX_REMISE`) sont déjà nommées : cet
/// exercice ne concerne *que* Extract Method. Le refactoring Replace Magic Number fera l'objet de
/// l'exercice 2 sur un autre exemple.
///
/// Refactoring attendu :
///
/// - **Extract Method** : extraire `sommeHT(Article[])`, `appliquerTVA(double)`,
///   `appliquerRemise(double)` comme méthodes privées. Le corps de `calculerTotal` doit se
///   réduire à la composition de ces trois appels.
public class Facture {

  private static final double TAUX_TVA = 1.20;
  private static final double SEUIL_REMISE = 100.0;
  private static final double TAUX_REMISE = 0.9;

  /// Calcule le montant total TTC d'une facture, avec remise au-delà du seuil.
  ///
  /// @param articles liste des articles de la facture
  /// @return montant total TTC, remise déduite le cas échéant
  public double calculerTotal(Article[] articles) {
    // somme des HT
    double total = 0;
    for (Article a : articles) {
      total += a.prixUnitaireHT() * a.quantite();
    }
    // TVA
    total = total * TAUX_TVA;
    // remise au-delà du seuil
    if (total > SEUIL_REMISE) {
      total = total * TAUX_REMISE;
    }
    return total;
  }
}
