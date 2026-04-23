package fr.univ_amu.iut.exercice1;

/**
 * Exercice 1 - Long Method : Extract Method.
 *
 * <p>Cette classe fonctionne : les tests de caractérisation la valident. Mais {@link
 * #calculerTotal} est un <b>Long Method</b> : elle fait trois choses à la suite (calcul HT,
 * application TVA, application remise). On veut que chacune devienne une méthode privée avec un nom
 * qui dit ce qu'elle fait, et que {@code calculerTotal} se lise comme un résumé de haut niveau.
 *
 * <p>Les constantes métier ({@code TAUX_TVA}, {@code SEUIL_REMISE}, {@code TAUX_REMISE}) sont déjà
 * nommées : cet exercice ne concerne <em>que</em> Extract Method. Le refactoring Replace Magic
 * Number fera l'objet de l'exercice 2 sur un autre exemple.
 *
 * <p>Refactoring attendu :
 *
 * <ul>
 *   <li><b>Extract Method</b> : extraire {@code sommeHT(Article[])}, {@code appliquerTVA(double)},
 *       {@code appliquerRemise(double)} comme méthodes privées. Le corps de {@code calculerTotal}
 *       doit se réduire à la composition de ces trois appels.
 * </ul>
 */
public class Facture {

  private static final double TAUX_TVA = 1.20;
  private static final double SEUIL_REMISE = 100.0;
  private static final double TAUX_REMISE = 0.9;

  /**
   * Calcule le montant total TTC d'une facture, avec remise au-delà du seuil.
   *
   * @param articles liste des articles de la facture
   * @return montant total TTC, remise déduite le cas échéant
   */
  public double calculerTotal(Article[] articles) {
    // --solution--
    return appliquerRemise(appliquerTVA(sommeHT(articles)));
    // --end-solution--
    /* --student--
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
    --end-student-- */
  }

  // --solution--
  private double sommeHT(Article[] articles) {
    double total = 0;
    for (Article a : articles) {
      total += a.prixUnitaireHT() * a.quantite();
    }
    return total;
  }

  private double appliquerTVA(double totalHT) {
    return totalHT * TAUX_TVA;
  }

  private double appliquerRemise(double totalTTC) {
    if (totalTTC > SEUIL_REMISE) {
      return totalTTC * TAUX_REMISE;
    }
    return totalTTC;
  }
  // --end-solution--
}
