package fr.univ_amu.iut.exercice6;

/**
 * Exercice 6 - Gilded Rose (capstone).
 *
 * <p>Ceci est le code existant de gestion de l'inventaire de la Gilded Rose. La direction veut
 * ajouter les articles <em>Conjured</em> (qui se dégradent deux fois plus vite que les articles
 * normaux). Mais avant de le faire, elle demande qu'on nettoie le code - parce qu'actuellement,
 * personne n'ose y toucher.
 *
 * <p>Règles en vigueur :
 *
 * <ul>
 *   <li>À la fin de chaque journée, {@code sellIn} et {@code quality} de chaque article sont mis à
 *       jour
 *   <li>Une fois {@code sellIn} passé (négatif), la qualité se dégrade <b>deux fois plus vite</b>
 *   <li>La qualité n'est jamais négative
 *   <li>La qualité d'un article n'est jamais au-dessus de 50... sauf pour "Sulfuras" qui est
 *       toujours à 80
 *   <li>"Aged Brie" <em>augmente</em> en qualité avec le temps
 *   <li>"Sulfuras, Hand of Ragnaros" ne doit jamais être vendu (sellIn ne change pas) et jamais se
 *       dégrader (quality ne change pas)
 *   <li>"Backstage passes to a TAFKAL80ETC concert" augmente en qualité :
 *       <ul>
 *         <li>de 2 quand il reste 10 jours ou moins
 *         <li>de 3 quand il reste 5 jours ou moins
 *         <li>tombe à 0 après le concert (sellIn &lt; 0)
 *       </ul>
 * </ul>
 *
 * <p>Votre mission :
 *
 * <ol>
 *   <li>Écrire des tests de caractérisation couvrant <b>toutes</b> les règles (déjà fournis)
 *   <li>Refactorer {@code updateQuality()} en gardant les tests verts - par exemple en extrayant
 *       une classe par type d'article (polymorphisme)
 *   <li>Ajouter le support des articles "Conjured" qui se dégradent deux fois plus vite (test dédié
 *       à activer une fois votre refactoring prêt)
 * </ol>
 *
 * <p>Contrainte : la classe {@link Item} ne doit pas changer (signature figée par la direction).
 */
public class GildedRose {

  Item[] items;

  public GildedRose(Item[] items) {
    this.items = items;
  }

  public void updateQuality() {
    // --solution--
    for (Item item : items) {
      updaterFor(item).apply(item);
    }
    // --end-solution--
    /* --student--
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
    --end-student-- */
  }

  // --solution--
  private static Updater updaterFor(Item item) {
    if (item.name.equals("Aged Brie")) {
      return new BrieUpdater();
    }
    if (item.name.equals("Sulfuras, Hand of Ragnaros")) {
      return new SulfurasUpdater();
    }
    if (item.name.equals("Backstage passes to a TAFKAL80ETC concert")) {
      return new BackstageUpdater();
    }
    if (item.name.startsWith("Conjured")) {
      return new ConjuredUpdater();
    }
    return new NormalUpdater();
  }
  // --end-solution--
}
