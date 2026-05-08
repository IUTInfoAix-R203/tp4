// --solution-only--
package fr.univ_amu.iut.exercice4;

/// Sous-classe d'[Animal] pour les vaches.
public class Vache extends Animal {

  public Vache(String nom) {
    super(nom);
  }

  @Override
  public String faireDuBruit() {
    return "Meuh !";
  }
}
