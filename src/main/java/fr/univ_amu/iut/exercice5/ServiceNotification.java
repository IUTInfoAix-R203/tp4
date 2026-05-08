package fr.univ_amu.iut.exercice5;

/// Exercice 5 - Introduce Parameter Object.
///
/// La méthode [#envoyer] prend **7 paramètres**. C'est le smell **Long Parameter List** dans
/// toute sa gloire :
///
/// - L'appel est illisible : `envoyer("a@b.c", "c@d.e", "Sujet", "Corps", true, 3, null)` - quel
///   booléen ? quel numéro ?
/// - Les paramètres sont **couplés** : destinataire, expéditeur et corps vont toujours ensemble ;
///   les dissocier n'a aucun sens métier
/// - Ajouter un 8e paramètre (ex: pièce jointe) impose de modifier **tous les appelants**
///
/// Refactoring attendu : **Introduce Parameter Object**. Créer une classe `MessageEmail` (un
/// `record` est parfait) qui regroupe les 7 paramètres. La méthode devient `envoyer(MessageEmail)`
/// : un seul paramètre, un contrat explicite, et ajouter un champ ne casse plus les appelants.
public class ServiceNotification {

  /// Formate et envoie (simulation) un email.
  ///
  /// @return la représentation textuelle du mail envoyé, pour vérification en test
  // --solution--
  // Delegate backward-compat : on garde la signature a 7 parametres pour ne pas
  // casser les appelants, mais on supprime explicitement le warning PMD car le
  // code metier a migre vers MessageEmail (ci-dessous). Sur la branche etudiante,
  // ce @SuppressWarnings n'apparait pas : PMD signale donc bien le smell.
  @SuppressWarnings("PMD.ExcessiveParameterList")
  // --end-solution--
  public String envoyer(
      String destinataire,
      String expediteur,
      String sujet,
      String corps,
      boolean important,
      int priorite,
      String[] piecesJointes) {
    // --solution--
    // Apres refactoring, la methode a 7 parametres delegue simplement a la nouvelle
    // signature. Les appelants qui ne sont pas encore migres continuent de marcher.
    return envoyer(
        new MessageEmail(
            destinataire, expediteur, sujet, corps, important, priorite, piecesJointes));
    // --end-solution--
    /* --student--
    StringBuilder sb = new StringBuilder();
    if (important) {
      sb.append("[IMPORTANT] ");
    }
    sb.append("[P").append(priorite).append("] ");
    sb.append("De: ").append(expediteur).append(", A: ").append(destinataire).append("\n");
    sb.append("Sujet: ").append(sujet).append("\n");
    sb.append("Corps: ").append(corps);
    if (piecesJointes != null && piecesJointes.length > 0) {
      sb.append("\nPieces jointes: ");
      for (int i = 0; i < piecesJointes.length; i++) {
        if (i > 0) sb.append(", ");
        sb.append(piecesJointes[i]);
      }
    }
    return sb.toString();
    --end-student-- */
  }

  // --solution--
  /// Nouvelle signature canonique : un seul parametre qui regroupe l'ensemble du message.
  public String envoyer(MessageEmail message) {
    StringBuilder sb = new StringBuilder();
    if (message.important()) {
      sb.append("[IMPORTANT] ");
    }
    sb.append("[P").append(message.priorite()).append("] ");
    sb.append("De: ")
        .append(message.expediteur())
        .append(", A: ")
        .append(message.destinataire())
        .append("\n");
    sb.append("Sujet: ").append(message.sujet()).append("\n");
    sb.append("Corps: ").append(message.corps());
    String[] piecesJointes = message.piecesJointes();
    if (piecesJointes != null && piecesJointes.length > 0) {
      sb.append("\nPieces jointes: ");
      for (int i = 0; i < piecesJointes.length; i++) {
        if (i > 0) sb.append(", ");
        sb.append(piecesJointes[i]);
      }
    }
    return sb.toString();
  }
  // --end-solution--
}
