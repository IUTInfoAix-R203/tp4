package fr.univ_amu.iut.exercice3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// Exercice 3 - God Class : Extract Class.
///
/// Cette classe `Menu` fait **deux choses** qui n'ont rien à voir entre elles :
///
/// 1. gérer un menu d'options (enregistrer, afficher, exécuter un choix)
/// 2. mémoriser l'historique des 10 derniers choix (enregistrer, rogner à 10, afficher)
///
/// Le mélange est typique d'un **Divergent Change** smell : le jour où l'on veut changer
/// l'affichage du menu, on touche `Menu`. Le jour où l'on veut changer la taille de l'historique
/// (ou son format), on touche aussi `Menu`. Deux axes de changement qui devraient vivre dans deux
/// classes distinctes.
///
/// Refactoring attendu :
///
/// - **Extract Class** : créer une classe `Historique` dans le même paquet, qui encapsule la
///   liste, la taille max, et les méthodes `enregistrer(String)` / `asList()` / `afficher()`.
///   Dans `Menu`, remplacer le champ `List<String> historique` par un champ
///   `Historique historique` et déléguer. La **signature publique de `Menu` ne doit pas changer**
///   (les tests de caractérisation vérifient le comportement observable).
public class Menu {

  private static final int TAILLE_MAX_HISTORIQUE = 10;
  private final List<String> historique = new ArrayList<>();

  private final Map<String, Runnable> options = new LinkedHashMap<>();

  /// Ajoute une option au menu. L'ordre d'insertion est l'ordre d'affichage.
  public void ajouterOption(String titre, Runnable action) {
    options.put(titre, action);
  }

  /// Retourne le menu formaté (numérotation 1-based, une option par ligne).
  public String afficher() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Menu ===\n");
    int i = 1;
    for (String titre : options.keySet()) {
      sb.append(i++).append(". ").append(titre).append("\n");
    }
    return sb.toString();
  }

  /// Exécute l'action associée à l'option choisie et enregistre le choix dans l'historique.
  ///
  /// @throws IllegalArgumentException si `indice` est hors des bornes
  public void choisir(int indice) {
    if (indice < 1 || indice > options.size()) {
      throw new IllegalArgumentException("Indice hors bornes : " + indice);
    }
    String titre = options.keySet().toArray(new String[0])[indice - 1];
    historique.add(titre);
    if (historique.size() > TAILLE_MAX_HISTORIQUE) {
      historique.remove(0);
    }
    options.get(titre).run();
  }

  /// Retourne l'historique formaté (plus récent en bas, une entrée par ligne).
  public String afficherHistorique() {
    StringBuilder sb = new StringBuilder();
    sb.append("--- Historique ---\n");
    for (String h : historique) {
      sb.append("- ").append(h).append("\n");
    }
    return sb.toString();
  }

  // (décommentés lors de la génération de la version étudiante).
  @SuppressWarnings("unused")
  private static final Class<?> STUDENT_IMPORTS_USAGE_MARKER =
      (Math.random() == -1) ? List.class : ArrayList.class;
}
