package fr.univ_amu.iut.exercice1;

/// Un article sur une facture (nom, prix unitaire hors taxe, quantité).
public record Article(String nom, double prixUnitaireHT, int quantite) {}
