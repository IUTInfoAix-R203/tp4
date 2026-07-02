package fr.univ_amu.iut.exercice1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de l'exercice 1 : Facture (Extract Method).
 *
 * <p>Les tests sont rangés en deux catégories :
 *
 * <ul>
 *   <li>Tests de <b>caractérisation</b> : ils passent <em>dès le début</em> sur le code smelly et
 *       permettent de refactorer sans crainte. C'est le filet de sécurité classique avant toute
 *       transformation de code existant.
 *   <li>Tests de <b>structure</b> : ils vérifient par <em>réflexion</em> que les trois méthodes ont
 *       bien été extraites. Ils échouent tant que le refactoring n'est pas fait. Activez-les au fur
 *       et à mesure de vos transformations.
 * </ul>
 *
 * <p>Règle d'or : <b>ne désactivez jamais un test de caractérisation</b>. S'il casse, c'est que
 * votre refactoring a changé le comportement - c'est un bug à corriger, pas un test à modifier.
 */
class FactureTest {

  // =========================================================================
  // Tests de caractérisation (actifs dès le début, doivent rester verts)
  // =========================================================================

  @Test
  void la_facture_vide_a_un_total_de_0() {
    assertThat(new Facture().calculerTotal(new Article[] {})).isZero();
  }

  @Test
  void la_facture_avec_un_seul_article_sous_le_seuil_applique_la_tva_sans_remise() {
    // 50 HT * 1.20 = 60 TTC, pas de remise car 60 < 100
    Article[] articles = {new Article("pain", 10.0, 5)};
    assertThat(new Facture().calculerTotal(articles)).isEqualTo(60.0, within(0.001));
  }

  @Test
  void la_facture_avec_un_seul_article_sur_le_seuil_applique_la_tva_et_la_remise() {
    // 100 HT * 1.20 = 120 TTC > 100, remise -> 120 * 0.9 = 108
    Article[] articles = {new Article("parapluie", 100.0, 1)};
    assertThat(new Facture().calculerTotal(articles)).isEqualTo(108.0, within(0.001));
  }

  @Test
  void la_facture_avec_plusieurs_articles_agrege_correctement() {
    Article[] articles = {
      new Article("pain", 2.0, 3), // 6
      new Article("fromage", 8.0, 2), // 16
      new Article("vin", 15.0, 1) // 15
    };
    // Total HT = 37, TTC = 44.4, pas de remise
    assertThat(new Facture().calculerTotal(articles)).isEqualTo(44.4, within(0.001));
  }

  @Test
  void la_facture_avec_un_total_exactement_a_100_ne_declenche_pas_la_remise() {
    // 83.33... HT * 1.20 = 100 TTC exactement ; la condition est "> 100" stricte
    Article[] articles = {new Article("test", 83.333333, 1)};
    // 83.333333 * 1.20 = 99.9999996, pas de remise
    assertThat(new Facture().calculerTotal(articles)).isEqualTo(99.9999996, within(0.001));
  }

  // =========================================================================
  // Tests de structure (à activer au fur et à mesure du refactoring)
  // =========================================================================

  @Disabled("Activer après avoir extrait la méthode sommeHT")
  @Test
  void la_methode_somme_ht_a_ete_extraite() throws Exception {
    Method m = Facture.class.getDeclaredMethod("sommeHT", Article[].class);
    m.setAccessible(true);
    Article[] articles = {new Article("a", 10.0, 2), new Article("b", 5.0, 3)};
    assertThat((double) m.invoke(new Facture(), (Object) articles)).isEqualTo(35.0, within(0.001));
  }

  @Disabled("Activer après avoir extrait la méthode appliquerTVA")
  @Test
  void la_methode_appliquer_tva_a_ete_extraite() throws Exception {
    Method m = Facture.class.getDeclaredMethod("appliquerTVA", double.class);
    m.setAccessible(true);
    assertThat((double) m.invoke(new Facture(), 50.0)).isEqualTo(60.0, within(0.001));
  }

  @Disabled("Activer après avoir extrait la méthode appliquerRemise")
  @Test
  void la_methode_appliquer_remise_a_ete_extraite() throws Exception {
    Method m = Facture.class.getDeclaredMethod("appliquerRemise", double.class);
    m.setAccessible(true);
    assertThat((double) m.invoke(new Facture(), 200.0))
        .isEqualTo(180.0, within(0.001)); // 200 * 0.9
    assertThat((double) m.invoke(new Facture(), 50.0))
        .isEqualTo(50.0, within(0.001)); // pas de remise
  }

  @Disabled("Activer après avoir raccourci calculerTotal")
  @Test
  void la_methode_calculer_total_est_courte_apres_extraction() throws Exception {
    // Après Extract Method, calculerTotal est court (par exemple 3 lignes de composition).
    // On ne peut pas compter les lignes directement via réflexion, mais on peut vérifier
    // que les 3 méthodes extraites existent et sont bien utilisées (tests précédents).
    // Ici on vérifie juste que calculerTotal reste accessible.
    Method m = Facture.class.getDeclaredMethod("calculerTotal", Article[].class);
    assertThat(m.getModifiers() & java.lang.reflect.Modifier.PUBLIC).isNotZero();
  }

  // (décommentés lors de la génération de la version étudiante).
  @SuppressWarnings("unused")
  private static final Class<?> DISABLED_USAGE_MARKER = Disabled.class;
}
