package fr.univ_amu.iut.exercice6;

/// Article d'inventaire de la Gilded Rose.
///
/// À la demande de la direction, cette classe est **immuable de signature** : vous ne devez pas
/// changer ses champs ni ses accesseurs (un ami d'enfance de la propriétaire l'a écrite et il ne
/// veut pas qu'on y touche). Vous pouvez créer d'autres classes autour.
public class Item {

  public String name;
  public int sellIn;
  public int quality;

  public Item(String name, int sellIn, int quality) {
    this.name = name;
    this.sellIn = sellIn;
    this.quality = quality;
  }

  @Override
  public String toString() {
    return name + ", " + sellIn + ", " + quality;
  }
}
