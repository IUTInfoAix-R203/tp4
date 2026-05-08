// --solution-only--
package fr.univ_amu.iut.exercice4;

/// Sous-classe d'[Animal] pour les chiens.
public class Chien extends Animal {

  public Chien(String nom) {
    super(nom);
  }

  @Override
  public String faireDuBruit() {
    return "Wouaf !";
  }
}
