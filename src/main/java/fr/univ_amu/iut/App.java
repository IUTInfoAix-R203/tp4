package fr.univ_amu.iut;

import fr.univ_amu.iut.exercice1.Article;
import fr.univ_amu.iut.exercice1.Facture;
import fr.univ_amu.iut.exercice2.CalculPrix;
import fr.univ_amu.iut.exercice3.Menu;
import fr.univ_amu.iut.exercice4.Animal;
import fr.univ_amu.iut.exercice5.ServiceNotification;
import fr.univ_amu.iut.exercice6.GildedRose;
import fr.univ_amu.iut.exercice6.Item;
import java.util.LinkedHashMap;
import java.util.Scanner;

/** Lanceur console du TP4 - Refactoring. */
public class App {

  public static void main(String[] args) {
    java.util.Map<String, Runnable> exercices = construireMenu();
    afficherMenu(exercices);
    if (exercices.isEmpty()) return;
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.print("Votre choix : ");
      if (!scanner.hasNextInt()) {
        System.out.println("Entrée invalide.");
        return;
      }
      lancerExercice(exercices, scanner.nextInt());
    }
  }

  static java.util.Map<String, Runnable> construireMenu() {
    java.util.Map<String, Runnable> exercices = new LinkedHashMap<>();
    exercices.put("Exercice 1 - Facture (Extract Method)", App::demoFacture);
    exercices.put("Exercice 2 - CalculPrix (Replace Magic Number)", App::demoCalculPrix);
    exercices.put("Exercice 3 - Menu (Extract Class)", App::demoMenu);
    exercices.put("Exercice 4 - Animal (Replace Conditional with Polymorphism)", App::demoAnimal);
    exercices.put("Exercice 5 - ServiceNotification (Introduce Parameter Object)", App::demoEmail);
    exercices.put("Exercice 6 - Gilded Rose (capstone)", App::demoGildedRose);
    return exercices;
  }

  static void afficherMenu(java.util.Map<String, Runnable> exercices) {
    System.out.println("=== TP4 - Refactoring - IUT Aix-Marseille ===");
    System.out.println();
    int i = 1;
    for (String titre : exercices.keySet()) {
      System.out.printf("  %d. %s%n", i++, titre);
    }
  }

  static void lancerExercice(java.util.Map<String, Runnable> exercices, int choix) {
    if (choix < 1 || choix > exercices.size()) {
      System.out.println("Choix hors des bornes.");
      return;
    }
    String titre = exercices.keySet().toArray(new String[0])[choix - 1];
    System.out.println();
    System.out.println("--- Lancement : " + titre + " ---");
    exercices.get(titre).run();
  }

  // ==================== Démos ====================

  private static void demoFacture() {
    Article[] articles = {new Article("pain", 2.0, 3), new Article("fromage", 8.0, 2)};
    System.out.printf("Total TTC : %.2f EUR%n", new Facture().calculerTotal(articles));
  }

  private static void demoCalculPrix() {
    CalculPrix c = new CalculPrix();
    System.out.printf("30 HT non fidele : %.2f EUR%n", c.calculerPrixFinal(30.0, false));
    System.out.printf("500 HT non fidele : %.2f EUR%n", c.calculerPrixFinal(500.0, false));
    System.out.printf("500 HT fidele : %.2f EUR%n", c.calculerPrixFinal(500.0, true));
    System.out.printf("1000 HT fidele : %.2f EUR%n", c.calculerPrixFinal(1000.0, true));
  }

  private static void demoMenu() {
    Menu m = new Menu();
    m.ajouterOption("Nouveau", () -> System.out.println("  >> Nouveau"));
    m.ajouterOption("Ouvrir", () -> System.out.println("  >> Ouvrir"));
    m.ajouterOption("Quitter", () -> System.out.println("  >> Quitter"));
    System.out.print(m.afficher());
    System.out.println("(simulation : choix 1, 3, 1)");
    m.choisir(1);
    m.choisir(3);
    m.choisir(1);
    System.out.print(m.afficherHistorique());
  }

  private static void demoAnimal() {
    for (String type : new String[] {"chien", "chat", "vache", "canard"}) {
      System.out.printf("  %s : %s%n", type, new Animal(type, type).faireDuBruit());
    }
  }

  private static void demoEmail() {
    String mail =
        new ServiceNotification()
            .envoyer(
                "etudiant@univ-amu.fr",
                "prof@univ-amu.fr",
                "TP4 à rendre",
                "Prière de rendre avant la fin de la séance",
                true,
                1,
                new String[] {"consignes.pdf"});
    System.out.println(mail);
  }

  private static void demoGildedRose() {
    Item[] items = {
      new Item("normal", 5, 10),
      new Item("Aged Brie", 5, 10),
      new Item("Sulfuras, Hand of Ragnaros", 0, 80),
      new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20)
    };
    System.out.println("Jour 0 :");
    for (Item it : items) System.out.println("  " + it);
    new GildedRose(items).updateQuality();
    System.out.println("Jour 1 :");
    for (Item it : items) System.out.println("  " + it);
  }
}
