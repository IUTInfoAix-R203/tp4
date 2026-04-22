package fr.univ_amu.iut.exercice2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de l'exercice 2 : Animal.
 *
 * <p>Tests de caractérisation d'abord (actifs). Tests de structure ensuite (à activer après le
 * refactoring vers la hiérarchie de classes).
 */
class AnimalTest {

  // =========================================================================
  // Caractérisation : chaque espèce fait bien son bruit
  // =========================================================================

  @Test
  void chien_waouf() {
    assertThat(new Animal("Rex", "chien").faireDuBruit()).isEqualTo("Wouaf !");
  }

  @Test
  void chat_miaou() {
    assertThat(new Animal("Felix", "chat").faireDuBruit()).isEqualTo("Miaou !");
  }

  @Test
  void vache_meuh() {
    assertThat(new Animal("Marguerite", "vache").faireDuBruit()).isEqualTo("Meuh !");
  }

  @Test
  void canard_coinCoin() {
    assertThat(new Animal("Donald", "canard").faireDuBruit()).isEqualTo("Coin coin !");
  }

  @Test
  void nomDoitEtreAccessible() {
    assertThat(new Animal("Rex", "chien").getNom()).isEqualTo("Rex");
  }

  // =========================================================================
  // Structure : activer après avoir remplacé le switch par du polymorphisme
  // =========================================================================

  @Disabled("Activer après Replace Conditional with Polymorphism (Animal devient abstract)")
  @Test
  void animalEstAbstract() {
    int m = Animal.class.getModifiers();
    assertThat(java.lang.reflect.Modifier.isAbstract(m))
        .as("Animal doit être abstract après le refactoring")
        .isTrue();
  }

  @Disabled("Activer après avoir créé la classe Chien extends Animal")
  @Test
  void classeChienHeriteDAnimal() throws Exception {
    Class<?> chien = Class.forName("fr.univ_amu.iut.exercice2.Chien");
    assertThat(Animal.class.isAssignableFrom(chien)).as("Chien doit hériter d'Animal").isTrue();
  }

  @Disabled("Activer après avoir créé les classes Chat, Vache, Canard extends Animal")
  @Test
  void toutesLesEspecesOntLeurClasse() throws Exception {
    for (String espece : new String[] {"Chat", "Vache", "Canard"}) {
      Class<?> c = Class.forName("fr.univ_amu.iut.exercice2." + espece);
      assertThat(Animal.class.isAssignableFrom(c)).as("%s doit hériter d'Animal", espece).isTrue();
    }
  }

  @Disabled("Activer après avoir retiré le champ type (String) d'Animal")
  @Test
  void champTypeStringSupprime() {
    assertThat(Animal.class.getDeclaredFields())
        .as("Aucun champ type:String ne doit plus exister")
        .noneMatch(f -> f.getName().equals("type") && f.getType() == String.class);
  }

  @Disabled("Activer après avoir créé une méthode statique de fabrique (Animal.creer(nom, type))")
  @Test
  void fabriqueCreeLaBonneEspece() throws Exception {
    // Pour maintenir la compatibilité avec du code existant, on peut garder un point d'entrée
    // par nom de type : Animal.creer("chien", "Rex") retourne un Chien.
    java.lang.reflect.Method creer =
        Animal.class.getDeclaredMethod("creer", String.class, String.class);
    Object rex = creer.invoke(null, "chien", "Rex");
    assertThat(rex.getClass().getSimpleName()).isEqualTo("Chien");
    assertThat(((Animal) rex).faireDuBruit()).isEqualTo("Wouaf !");
  }
}
