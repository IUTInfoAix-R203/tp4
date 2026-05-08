// --solution-only--
package fr.univ_amu.iut.exercice6;

/// Stratégie d'évolution quotidienne d'un [Item].
///
/// Un `Updater` par type d'article. Chacun encapsule la règle propre à son type, en utilisant
/// les helpers `increaseQuality`, `decreaseQuality`, `decreaseSellIn` pour exprimer l'invariant
/// "la qualité est entre 0 et 50". [GildedRose] sélectionne le bon updater par le nom de
/// l'article et lui délègue la mise à jour.
///
/// Ce fichier (et les sous-classes qu'il contient) n'existe **que sur la solution** du
/// refactoring. Dans la version étudiante, c'est à vous de décomposer le switch en classes.
abstract class Updater {

  abstract void apply(Item item);

  protected void increaseQuality(Item item) {
    if (item.quality < 50) {
      item.quality++;
    }
  }

  protected void decreaseQuality(Item item) {
    if (item.quality > 0) {
      item.quality--;
    }
  }

  protected void decreaseSellIn(Item item) {
    item.sellIn--;
  }
}

/// Article normal : qualité -1 par jour, -2 après péremption.
class NormalUpdater extends Updater {
  @Override
  void apply(Item item) {
    decreaseQuality(item);
    decreaseSellIn(item);
    if (item.sellIn < 0) {
      decreaseQuality(item);
    }
  }
}

/// Aged Brie : qualité +1 par jour, +2 après péremption, plafonnée à 50.
class BrieUpdater extends Updater {
  @Override
  void apply(Item item) {
    increaseQuality(item);
    decreaseSellIn(item);
    if (item.sellIn < 0) {
      increaseQuality(item);
    }
  }
}

/// Sulfuras : jamais ne se vend, jamais ne se dégrade.
class SulfurasUpdater extends Updater {
  @Override
  void apply(Item item) {
    // rien : ni sellIn ni quality ne changent
  }
}

/// Backstage passes : qualité +1 jusqu'à 11 jours, +2 entre 10 et 6, +3 à 5 jours et moins, tombe
/// à 0 après le concert.
class BackstageUpdater extends Updater {
  @Override
  void apply(Item item) {
    increaseQuality(item);
    if (item.sellIn < 11) {
      increaseQuality(item);
    }
    if (item.sellIn < 6) {
      increaseQuality(item);
    }
    decreaseSellIn(item);
    if (item.sellIn < 0) {
      item.quality = 0;
    }
  }
}

/// Conjured : se dégrade deux fois plus vite qu'un article normal.
class ConjuredUpdater extends Updater {
  @Override
  void apply(Item item) {
    decreaseQuality(item);
    decreaseQuality(item);
    decreaseSellIn(item);
    if (item.sellIn < 0) {
      decreaseQuality(item);
      decreaseQuality(item);
    }
  }
}
