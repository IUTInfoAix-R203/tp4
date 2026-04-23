// --solution-only--
package fr.univ_amu.iut.exercice5;

/**
 * Parameter Object extrait des 7 paramètres de {@link ServiceNotification#envoyer}.
 *
 * <p>Regroupe les informations d'un email : qui, à qui, quel sujet, quel corps, urgence et
 * priorité, pièces jointes éventuelles. Rassembler ces champs dans un seul objet rend les appels
 * auto-documentés et permet d'ajouter un champ sans casser les appelants.
 *
 * <p>Cette classe n'existe <b>que sur la solution</b> du refactoring. Dans la version étudiante du
 * TP, elle n'est pas présente : c'est à vous de la créer.
 */
public record MessageEmail(
    String destinataire,
    String expediteur,
    String sujet,
    String corps,
    boolean important,
    int priorite,
    String[] piecesJointes) {}
