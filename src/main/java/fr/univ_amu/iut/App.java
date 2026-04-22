package fr.univ_amu.iut;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Lanceur console du TP TP4.
 *
 * Affiche la liste des exercices disponibles et permet d'en lancer un
 * en lisant un numéro sur l'entrée standard. Ajoutez chaque exercice
 * dans la Map {@link #construireMenu()} quand vous le créez.
 */
public class App {

    public static void main(String[] args) {
        Map<String, Runnable> exercices = construireMenu();
        afficherMenu(exercices);

        if (exercices.isEmpty()) {
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Votre choix : ");
            if (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide.");
                return;
            }
            lancerExercice(exercices, scanner.nextInt());
        }
    }

    static Map<String, Runnable> construireMenu() {
        Map<String, Runnable> exercices = new LinkedHashMap<>();
        // ========== EXERCICES - Ajouter les lancements ici ==========
        // exercices.put("Exercice 1 - Hello World",
        //     () -> fr.univ_amu.iut.exercice1.HelloWorld.main(new String[0]));
        // exercices.put("Exercice 2 - FizzBuzz",
        //     () -> fr.univ_amu.iut.exercice2.FizzBuzz.main(new String[0]));
        return exercices;
    }

    static void afficherMenu(Map<String, Runnable> exercices) {
        System.out.println("=== TP4 - IUT Aix-Marseille ===");
        System.out.println();
        if (exercices.isEmpty()) {
            System.out.println("Aucun exercice disponible.");
            System.out.println("Ajoutez des exercices dans App.construireMenu() et relancez.");
            return;
        }
        int i = 1;
        for (String titre : exercices.keySet()) {
            System.out.printf("  %d. %s%n", i++, titre);
        }
    }

    static void lancerExercice(Map<String, Runnable> exercices, int choix) {
        if (choix < 1 || choix > exercices.size()) {
            System.out.println("Choix hors des bornes.");
            return;
        }
        String titre = exercices.keySet().toArray(new String[0])[choix - 1];
        System.out.println();
        System.out.println("--- Lancement : " + titre + " ---");
        exercices.get(titre).run();
    }
}
