// --solution-only--
package fr.univ_amu.iut.exercice4;

/** Sous-classe d'{@link Animal} pour les chats. */
public class Chat extends Animal {

  public Chat(String nom) {
    super(nom);
  }

  @Override
  public String faireDuBruit() {
    return "Miaou !";
  }
}
