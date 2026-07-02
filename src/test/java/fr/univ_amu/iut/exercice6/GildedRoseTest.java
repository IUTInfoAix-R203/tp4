package fr.univ_amu.iut.exercice6;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de caractérisation de la Gilded Rose.
 *
 * <p>Ces tests pinnent le comportement <b>actuel</b> du code, qu'il soit "juste" ou non. Leur
 * mission : vous permettre de refactorer sans crainte. Si un de ces tests casse, votre refactoring
 * a changé le comportement - ce n'est plus un refactoring, c'est un bug.
 *
 * <p>Une fois votre refactoring terminé, activez les tests "Conjured" pour ajouter cette nouvelle
 * fonctionnalité.
 */
class GildedRoseTest {

  // =========================================================================
  //  Articles normaux
  // =========================================================================

  @Test
  void un_item_normal_avant_peremption_baisse_quality_de_1_et_sellin_de_1() {
    Item[] items = {new Item("normal", 5, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].sellIn).isEqualTo(4);
    assertThat(items[0].quality).isEqualTo(9);
  }

  @Test
  void un_item_normal_apres_peremption_baisse_quality_de_2() {
    Item[] items = {new Item("normal", 0, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].sellIn).isEqualTo(-1);
    assertThat(items[0].quality).isEqualTo(8);
  }

  @Test
  void un_item_normal_ne_voit_jamais_sa_quality_devenir_negative() {
    Item[] items = {new Item("normal", 5, 0)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isZero();
  }

  // =========================================================================
  //  Aged Brie
  // =========================================================================

  @Test
  void un_aged_brie_augmente_en_quality_avec_le_temps() {
    Item[] items = {new Item("Aged Brie", 5, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(11);
  }

  @Test
  void un_aged_brie_apres_peremption_augmente_deux_fois_plus_vite() {
    Item[] items = {new Item("Aged Brie", 0, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(12);
  }

  @Test
  void un_aged_brie_ne_depasse_jamais_50_en_quality() {
    Item[] items = {new Item("Aged Brie", 5, 50)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(50);
  }

  // =========================================================================
  //  Sulfuras
  // =========================================================================

  @Test
  void un_sulfuras_n_est_jamais_vendu_ni_degrade() {
    Item[] items = {new Item("Sulfuras, Hand of Ragnaros", 0, 80)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].sellIn).isZero();
    assertThat(items[0].quality).isEqualTo(80);
  }

  @Test
  void un_sulfuras_conserve_sa_quality_quelle_que_soit_la_date() {
    Item[] items = {new Item("Sulfuras, Hand of Ragnaros", -5, 80)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].sellIn).isEqualTo(-5);
    assertThat(items[0].quality).isEqualTo(80);
  }

  // =========================================================================
  //  Backstage passes
  // =========================================================================

  @Test
  void un_backstage_a_plus_de_10_jours_augmente_de_1() {
    Item[] items = {new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(21);
  }

  @Test
  void un_backstage_a_10_jours_ou_moins_augmente_de_2() {
    Item[] items = {new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(22);
  }

  @Test
  void un_backstage_a_5_jours_ou_moins_augmente_de_3() {
    Item[] items = {new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(23);
  }

  @Test
  void un_backstage_apres_le_concert_tombe_a_zero() {
    Item[] items = {new Item("Backstage passes to a TAFKAL80ETC concert", 0, 49)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isZero();
  }

  @Test
  void un_backstage_ne_depasse_pas_50() {
    Item[] items = {new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(50);
  }

  // =========================================================================
  //  Conjured (nouvelle fonctionnalité - à activer après le refactoring)
  // =========================================================================

  @Disabled("Activer après avoir refactoré et ajouté le support des articles Conjured")
  @Test
  void un_conjured_avant_peremption_baisse_de_2() {
    Item[] items = {new Item("Conjured Mana Cake", 5, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(8);
  }

  @Disabled("Activer après avoir refactoré et ajouté le support des articles Conjured")
  @Test
  void un_conjured_apres_peremption_baisse_de_4() {
    Item[] items = {new Item("Conjured Mana Cake", 0, 10)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isEqualTo(6);
  }

  @Disabled("Activer après avoir refactoré et ajouté le support des articles Conjured")
  @Test
  void un_conjured_ne_devient_pas_negatif() {
    Item[] items = {new Item("Conjured Mana Cake", 5, 1)};
    new GildedRose(items).updateQuality();
    assertThat(items[0].quality).isZero();
  }

  @SuppressWarnings("unused")
  private static final Class<?> DISABLED_USAGE_MARKER = Disabled.class;
}
