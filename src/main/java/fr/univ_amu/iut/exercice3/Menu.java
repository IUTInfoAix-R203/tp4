package fr.univ_amu.iut.exercice3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercice 3 - God Class : Extract Class.
 *
 * <p>Cette classe {@code Menu} fait <b>deux choses</b> qui n'ont rien à voir entre elles :
 *
 * <ol>
 *   <li>gérer un menu d'options (enregistrer, afficher, exécuter un choix)
 *   <li>mémoriser l'historique des 10 derniers choix (enregistrer, rogner à 10, afficher)
 * </ol>
 *
 * <p>Le mélange est typique d'un <b>Divergent Change</b> smell : le jour où l'on veut changer
 * l'affichage du menu, on touche {@code Menu}. Le jour où l'on veut changer la taille de
 * l'historique (ou son format), on touche aussi {@code Menu}. Deux axes de changement qui devraient
 * vivre dans deux classes distinctes.
 *
 * <p>Refactoring attendu :
 *
 * <ul>
 *   <li><b>Extract Class</b> : créer une classe {@code Historique} dans le même paquet, qui
 *       encapsule la liste, la taille max, et les méthodes {@code enregistrer(String)} / {@code
 *       asList()} / {@code afficher()}. Dans {@code Menu}, remplacer le champ {@code List<String>
 *       historique} par un champ {@code Historique historique} et déléguer. La <b>signature
 *       publique de {@code Menu} ne doit pas changer</b> (les tests de caractérisation vérifient le
 *       comportement observable).
 * </ul>
 */
public class Menu {

  // --solution--
  private final Historique historique = new Historique();
  // --end-solution--
  /* --student--
  private static final int TAILLE_MAX_HISTORIQUE = 10;
  private final List<String> historique = new ArrayList<>();
  --end-student-- */

  private final Map<String, Runnable> options = new LinkedHashMap<>();

  /** Ajoute une option au menu. L'ordre d'insertion est l'ordre d'affichage. */
  public void ajouterOption(String titre, Runnable action) {
    options.put(titre, action);
  }

  /** Retourne le menu formaté (numérotation 1-based, une option par ligne). */
  public String afficher() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Menu ===\n");
    int i = 1;
    for (String titre : options.keySet()) {
      sb.append(i++).append(". ").append(titre).append("\n");
    }
    return sb.toString();
  }

  /**
   * Exécute l'action associée à l'option choisie et enregistre le choix dans l'historique.
   *
   * @throws IllegalArgumentException si {@code indice} est hors des bornes
   */
  public void choisir(int indice) {
    if (indice < 1 || indice > options.size()) {
      throw new IllegalArgumentException("Indice hors bornes : " + indice);
    }
    String titre = options.keySet().toArray(new String[0])[indice - 1];
    // --solution--
    historique.enregistrer(titre);
    // --end-solution--
    /* --student--
    historique.add(titre);
    if (historique.size() > TAILLE_MAX_HISTORIQUE) {
      historique.remove(0);
    }
    --end-student-- */
    options.get(titre).run();
  }

  /** Retourne l'historique formaté (plus récent en bas, une entrée par ligne). */
  public String afficherHistorique() {
    // --solution--
    return historique.afficher();
    // --end-solution--
    /* --student--
    StringBuilder sb = new StringBuilder();
    sb.append("--- Historique ---\n");
    for (String h : historique) {
      sb.append("- ").append(h).append("\n");
    }
    return sb.toString();
    --end-student-- */
  }

  // Imports List / ArrayList conservés : utilisés dans les blocs /* --student-- */
  // (décommentés lors de la génération de la version étudiante).
  @SuppressWarnings("unused")
  private static final Class<?> STUDENT_IMPORTS_USAGE_MARKER =
      (Math.random() == -1) ? List.class : ArrayList.class;
}
