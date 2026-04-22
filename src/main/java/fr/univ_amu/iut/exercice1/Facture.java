package fr.univ_amu.iut.exercice1;

/**
 * Exercice 1 - Long Method + Magic Numbers.
 *
 * <p>Cette classe fonctionne : les tests de caractérisation la valident. Mais elle pue. Trois
 * choses qui clochent au premier coup d'œil :
 *
 * <ol>
 *   <li>{@link #calculerTotal} fait tout en une seule méthode (Long Method smell) : calcul HT,
 *       application TVA, application remise
 *   <li>Les valeurs {@code 1.20}, {@code 100}, {@code 0.9} sont des nombres magiques (Magic Number
 *       smell) : on ne sait pas à quoi elles correspondent sans lire le code
 *   <li>Les commentaires décrivent ce que fait le code ligne à ligne, signe que les noms ne sont
 *       pas suffisamment parlants
 * </ol>
 *
 * <p>Votre mission : refactorer cette classe pour qu'elle devienne <b>lisible sans commentaire</b>
 * et <b>maintenable</b>. Les tests doivent rester verts à chaque étape.
 *
 * <p>Refactorings attendus (dans cet ordre recommandé) :
 *
 * <ol>
 *   <li><b>Replace Magic Number with Symbolic Constant</b> : extraire {@code TAUX_TVA}, {@code
 *       SEUIL_REMISE}, {@code TAUX_REMISE} comme {@code private static final double}
 *   <li><b>Extract Method</b> : extraire {@code sommeHT}, {@code appliquerTVA}, {@code
 *       appliquerRemise} comme méthodes privées. {@code calculerTotal} doit se réduire à une
 *       composition de 3 appels
 * </ol>
 */
public class Facture {

  /**
   * Calcule le montant total TTC d'une facture, avec remise de 10 % au-delà de 100 € HT + TVA.
   *
   * @param articles liste des articles de la facture
   * @return montant total TTC, remise déduite le cas échéant
   */
  public double calculerTotal(Article[] articles) {
    // somme des HT
    double total = 0;
    for (Article a : articles) {
      total += a.prixUnitaireHT() * a.quantite();
    }
    // TVA
    total = total * 1.20;
    // remise au-delà de 100
    if (total > 100) {
      total = total * 0.9;
    }
    return total;
  }
}
