// --solution-only--
package fr.univ_amu.iut.exercice4;

/// Sous-classe d'[Animal] pour les canards.
public class Canard extends Animal {

  public Canard(String nom) {
    super(nom);
  }

  @Override
  public String faireDuBruit() {
    return "Coin coin !";
  }
}
