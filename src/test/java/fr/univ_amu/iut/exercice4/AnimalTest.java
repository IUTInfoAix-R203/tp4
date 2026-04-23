package fr.univ_amu.iut.exercice4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de l'exercice 4 : Animal (Replace Conditional with Polymorphism).
 *
 * <p>Tests de caractérisation d'abord (actifs). Tests de structure ensuite (à activer après le
 * refactoring vers la hiérarchie de classes).
 *
 * <p>Les tests de caractérisation utilisent un helper {@link #creerAnimal(String, String)} pour
 * isoler la différence d'API entre la version smelly (constructeur à deux arguments) et la version
 * refactorisée (fabrique statique retournant la bonne sous-classe). Cela permet aux tests de
 * caractérisation de rester verts avant et après le refactoring, sans avoir à toucher leur code.
 */
class AnimalTest {

  // =========================================================================
  // Caractérisation : chaque espèce fait bien son bruit
  // =========================================================================

  @Test
  void chien_waouf() {
    assertThat(creerAnimal("Rex", "chien").faireDuBruit()).isEqualTo("Wouaf !");
  }

  @Test
  void chat_miaou() {
    assertThat(creerAnimal("Felix", "chat").faireDuBruit()).isEqualTo("Miaou !");
  }

  @Test
  void vache_meuh() {
    assertThat(creerAnimal("Marguerite", "vache").faireDuBruit()).isEqualTo("Meuh !");
  }

  @Test
  void canard_coinCoin() {
    assertThat(creerAnimal("Donald", "canard").faireDuBruit()).isEqualTo("Coin coin !");
  }

  @Test
  void nomDoitEtreAccessible() {
    assertThat(creerAnimal("Rex", "chien").getNom()).isEqualTo("Rex");
  }

  // =========================================================================
  // Structure : activer après avoir remplacé le switch par du polymorphisme
  // =========================================================================

  /* --student--
  @Disabled("Activer après Replace Conditional with Polymorphism (Animal devient abstract)")
  --end-student-- */
  @Test
  void animalEstAbstract() {
    int m = Animal.class.getModifiers();
    assertThat(java.lang.reflect.Modifier.isAbstract(m))
        .as("Animal doit être abstract après le refactoring")
        .isTrue();
  }

  /* --student--
  @Disabled("Activer après avoir créé la classe Chien extends Animal")
  --end-student-- */
  @Test
  void classeChienHeriteDAnimal() throws Exception {
    Class<?> chien = Class.forName("fr.univ_amu.iut.exercice4.Chien");
    assertThat(Animal.class.isAssignableFrom(chien)).as("Chien doit hériter d'Animal").isTrue();
  }

  /* --student--
  @Disabled("Activer après avoir créé les classes Chat, Vache, Canard extends Animal")
  --end-student-- */
  @Test
  void toutesLesEspecesOntLeurClasse() throws Exception {
    for (String espece : new String[] {"Chat", "Vache", "Canard"}) {
      Class<?> c = Class.forName("fr.univ_amu.iut.exercice4." + espece);
      assertThat(Animal.class.isAssignableFrom(c)).as("%s doit hériter d'Animal", espece).isTrue();
    }
  }

  /* --student--
  @Disabled("Activer après avoir retiré le champ type (String) d'Animal")
  --end-student-- */
  @Test
  void champTypeStringSupprime() {
    assertThat(Animal.class.getDeclaredFields())
        .as("Aucun champ type:String ne doit plus exister")
        .noneMatch(f -> f.getName().equals("type") && f.getType() == String.class);
  }

  /* --student--
  @Disabled("Activer après avoir créé une méthode statique de fabrique (Animal.creer(type, nom))")
  --end-student-- */
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

  // ------------------------------------------------------------------------
  //  Helper : isole la difference d'API entre smelly et refactored
  // ------------------------------------------------------------------------

  private static Animal creerAnimal(String nom, String type) {
    // --solution--
    return Animal.creer(type, nom);
    // --end-solution--
    /* --student--
    return new Animal(nom, type);
    --end-student-- */
  }

  @SuppressWarnings("unused")
  private static final Class<?> DISABLED_USAGE_MARKER = Disabled.class;
}
