package fr.univ_amu.iut.exercice4;

/**
 * Exercice 2 - Replace Conditional with Polymorphism.
 *
 * <p>Cette classe {@code Animal} a un champ {@code type} (une {@link String}...) et une méthode
 * {@link #faireDuBruit()} qui est un gros {@code switch} sur ce type. Chaque fois qu'on ajoute une
 * espèce, il faut modifier le switch - et risque d'oublier un cas.
 *
 * <p>Smells présents :
 *
 * <ul>
 *   <li><b>Type Code</b> : le champ {@code type} est un String alors qu'il représente une
 *       énumération fermée (chien, chat, vache, canard)
 *   <li><b>Switch Statements</b> : le switch duplique la logique "qui fait quel bruit"
 *   <li><b>Shotgun Surgery</b> : ajouter une nouvelle espèce demande de modifier potentiellement
 *       plusieurs méthodes (ici uniquement {@code faireDuBruit()}, mais imaginez si on ajoute
 *       {@code nourrir()}, {@code dormir()}, etc.)
 * </ul>
 *
 * <p>Refactoring attendu : <b>Replace Conditional with Polymorphism</b>. Transformer {@code Animal}
 * en classe abstraite, créer {@code Chien}, {@code Chat}, {@code Vache}, {@code Canard} qui
 * héritent et redéfinissent {@code faireDuBruit()}. Le switch disparaît ; la JVM s'occupe du
 * dispatch.
 */
public class Animal {

  private final String nom;
  private final String type; // "chien", "chat", "vache", "canard"

  public Animal(String nom, String type) {
    this.nom = nom;
    this.type = type;
  }

  public String getNom() {
    return nom;
  }

  public String getType() {
    return type;
  }

  /** Retourne le cri de l'animal selon son type. */
  public String faireDuBruit() {
    switch (type) {
      case "chien":
        return "Wouaf !";
      case "chat":
        return "Miaou !";
      case "vache":
        return "Meuh !";
      case "canard":
        return "Coin coin !";
      default:
        throw new IllegalStateException("Type inconnu : " + type);
    }
  }
}
