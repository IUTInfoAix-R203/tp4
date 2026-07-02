package fr.univ_amu.iut.exercice6;

/// Exercice 6 - Gilded Rose (capstone).
///
/// Ceci est le code existant de gestion de l'inventaire de la Gilded Rose. La direction veut
/// ajouter les articles *Conjured* (qui se dégradent deux fois plus vite que les articles
/// normaux). Mais avant de le faire, elle demande qu'on nettoie le code - parce qu'actuellement,
/// personne n'ose y toucher.
///
/// Règles en vigueur :
///
/// - À la fin de chaque journée, `sellIn` et `quality` de chaque article sont mis à jour
/// - Une fois `sellIn` passé (négatif), la qualité se dégrade **deux fois plus vite**
/// - La qualité n'est jamais négative
/// - La qualité d'un article n'est jamais au-dessus de 50... sauf pour "Sulfuras" qui est
///   toujours à 80
/// - "Aged Brie" *augmente* en qualité avec le temps
/// - "Sulfuras, Hand of Ragnaros" ne doit jamais être vendu (sellIn ne change pas) et jamais se
///   dégrader (quality ne change pas)
/// - "Backstage passes to a TAFKAL80ETC concert" augmente en qualité :
///   - de 2 quand il reste 10 jours ou moins
///   - de 3 quand il reste 5 jours ou moins
///   - tombe à 0 après le concert (sellIn < 0)
///
/// Votre mission :
///
/// 1. Écrire des tests de caractérisation couvrant **toutes** les règles (déjà fournis)
/// 2. Refactorer `updateQuality()` en gardant les tests verts - par exemple en extrayant une
///    classe par type d'article (polymorphisme)
/// 3. Ajouter le support des articles "Conjured" qui se dégradent deux fois plus vite (test dédié
///    à activer une fois votre refactoring prêt)
///
/// Contrainte : la classe [Item] ne doit pas changer (signature figée par la direction).
public class GildedRose {

  Item[] items;

  public GildedRose(Item[] items) {
    this.items = items;
  }

  public void updateQuality() {
    for (int i = 0; i < items.length; i++) {
      if (!items[i].name.equals("Aged Brie")
          && !items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
        if (items[i].quality > 0) {
          if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
            items[i].quality = items[i].quality - 1;
          }
        }
      } else {
        if (items[i].quality < 50) {
          items[i].quality = items[i].quality + 1;

          if (items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
            if (items[i].sellIn < 11) {
              if (items[i].quality < 50) {
                items[i].quality = items[i].quality + 1;
              }
            }

            if (items[i].sellIn < 6) {
              if (items[i].quality < 50) {
                items[i].quality = items[i].quality + 1;
              }
            }
          }
        }
      }

      if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
        items[i].sellIn = items[i].sellIn - 1;
      }

      if (items[i].sellIn < 0) {
        if (!items[i].name.equals("Aged Brie")) {
          if (!items[i].name.equals("Backstage passes to a TAFKAL80ETC concert")) {
            if (items[i].quality > 0) {
              if (!items[i].name.equals("Sulfuras, Hand of Ragnaros")) {
                items[i].quality = items[i].quality - 1;
              }
            }
          } else {
            items[i].quality = items[i].quality - items[i].quality;
          }
        } else {
          if (items[i].quality < 50) {
            items[i].quality = items[i].quality + 1;
          }
        }
      }
    }
  }
}
