package fr.univ_amu.iut.exercice3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de l'exercice 3 : Menu (Extract Class).
 *
 * <p>Les tests sont rangés en deux catégories :
 *
 * <ul>
 *   <li>Tests de <b>caractérisation</b> : ils valident le comportement observable de {@code Menu}
 *       (affichage, exécution d'un choix, troncature de l'historique à 10). Ils doivent rester
 *       verts à chaque étape du refactoring : la signature publique de {@code Menu} ne change pas.
 *   <li>Tests de <b>structure</b> : ils vérifient par <em>réflexion</em> que la classe {@code
 *       Historique} a bien été extraite et que {@code Menu} délègue via un champ de ce type.
 * </ul>
 */
class MenuTest {

  // =========================================================================
  // Tests de caractérisation (actifs dès le début, doivent rester verts)
  // =========================================================================

  @Test
  void afficher_un_menu_vide_retourne_l_entete() {
    assertThat(new Menu().afficher()).isEqualTo("=== Menu ===\n");
  }

  @Test
  void afficher_deux_options_les_liste_dans_l_ordre_d_insertion() {
    Menu m = new Menu();
    m.ajouterOption("Nouveau", () -> {});
    m.ajouterOption("Quitter", () -> {});
    assertThat(m.afficher()).isEqualTo("=== Menu ===\n1. Nouveau\n2. Quitter\n");
  }

  @Test
  void choisir_une_option_execute_l_action() {
    Menu m = new Menu();
    AtomicInteger compteur = new AtomicInteger();
    m.ajouterOption("Incrementer", compteur::incrementAndGet);
    m.choisir(1);
    assertThat(compteur.get()).isEqualTo(1);
  }

  @Test
  void choisir_un_indice_invalide_leve_une_exception() {
    Menu m = new Menu();
    m.ajouterOption("Unique", () -> {});
    assertThatThrownBy(() -> m.choisir(0)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> m.choisir(2)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void afficher_l_historique_vide_retourne_l_entete_seul() {
    assertThat(new Menu().afficherHistorique()).isEqualTo("--- Historique ---\n");
  }

  @Test
  void afficher_l_historique_apres_quelques_choix_les_liste_dans_l_ordre() {
    Menu m = new Menu();
    m.ajouterOption("A", () -> {});
    m.ajouterOption("B", () -> {});
    m.choisir(1);
    m.choisir(2);
    m.choisir(1);
    assertThat(m.afficherHistorique()).isEqualTo("--- Historique ---\n- A\n- B\n- A\n");
  }

  @Test
  void l_historique_est_tronque_a_10_le_onzieme_chasse_le_premier() {
    Menu m = new Menu();
    m.ajouterOption("X", () -> {});
    for (int i = 0; i < 11; i++) {
      m.choisir(1);
    }
    // 11 enregistrements, max 10 -> on doit avoir 10 lignes "- X"
    String contenu = m.afficherHistorique();
    long lignes = contenu.lines().filter(l -> l.equals("- X")).count();
    assertThat(lignes).isEqualTo(10);
  }

  // =========================================================================
  // Tests de structure (à activer au fur et à mesure du refactoring)
  // =========================================================================

  @Disabled("Activer après avoir créé la classe Historique")
  @Test
  void la_classe_historique_existe() throws Exception {
    Class<?> histo = Class.forName("fr.univ_amu.iut.exercice3.Historique");
    assertThat(histo).isNotNull();
  }

  @Disabled("Activer après avoir doté Historique des trois méthodes attendues")
  @Test
  void la_classe_historique_a_les_trois_methodes_attendues() throws Exception {
    Class<?> histo = Class.forName("fr.univ_amu.iut.exercice3.Historique");
    assertThat(histo.getDeclaredMethod("enregistrer", String.class))
        .as("Historique.enregistrer(String)")
        .isNotNull();
    assertThat(histo.getDeclaredMethod("asList")).as("Historique.asList()").isNotNull();
    assertThat(histo.getDeclaredMethod("afficher")).as("Historique.afficher()").isNotNull();
  }

  @Disabled("Activer après avoir remplacé le champ historique de Menu par un Historique")
  @Test
  void le_menu_utilise_un_champ_de_type_historique() throws Exception {
    Class<?> histo = Class.forName("fr.univ_amu.iut.exercice3.Historique");
    boolean trouve = false;
    for (Field f : Menu.class.getDeclaredFields()) {
      if (f.getType().equals(histo)) {
        trouve = true;
        break;
      }
    }
    assertThat(trouve)
        .as("Menu doit avoir un champ de type Historique après Extract Class")
        .isTrue();
  }

  @SuppressWarnings("unused")
  private static final Class<?> DISABLED_USAGE_MARKER = Disabled.class;
}
