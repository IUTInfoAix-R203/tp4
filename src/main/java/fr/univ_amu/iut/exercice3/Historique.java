// --solution-only--
package fr.univ_amu.iut.exercice3;

import java.util.ArrayList;
import java.util.List;

/// Classe extraite de [Menu] (refactoring Extract Class).
///
/// Encapsule un historique borné : les N derniers choix, dans leur ordre chronologique. Quand la
/// capacité max est atteinte, le plus ancien est rogné (file FIFO).
///
/// Cette classe n'existe **que sur la solution** du refactoring. Dans la version étudiante du
/// TP, elle n'est pas présente : c'est à vous de la créer.
public class Historique {

  private static final int TAILLE_MAX_PAR_DEFAUT = 10;

  private final int tailleMax;
  private final List<String> entrees = new ArrayList<>();

  public Historique() {
    this(TAILLE_MAX_PAR_DEFAUT);
  }

  public Historique(int tailleMax) {
    this.tailleMax = tailleMax;
  }

  /// Enregistre une entrée. Si la taille max est dépassée, rogne la plus ancienne.
  public void enregistrer(String entree) {
    entrees.add(entree);
    if (entrees.size() > tailleMax) {
      entrees.remove(0);
    }
  }

  /// Retourne une vue en lecture seule des entrées enregistrées.
  public List<String> asList() {
    return List.copyOf(entrees);
  }

  /// Retourne l'historique formaté (plus récent en bas, une entrée par ligne).
  public String afficher() {
    StringBuilder sb = new StringBuilder();
    sb.append("--- Historique ---\n");
    for (String h : entrees) {
      sb.append("- ").append(h).append("\n");
    }
    return sb.toString();
  }
}
