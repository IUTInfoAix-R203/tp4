package fr.univ_amu.iut.exercice4;

/**
 * Exercice 4 - Replace Conditional with Polymorphism.
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
 * dispatch. On garde un point d'entrée `Animal.creer(type, nom)` qui retourne la bonne sous-classe,
 * pour les appelants qui ne connaissent que le type sous forme de String.
 */
// --solution--
public abstract class Animal {

  private final String nom;

  protected Animal(String nom) {
    this.nom = nom;
  }

  public String getNom() {
    return nom;
  }

  /** Retourne le cri de l'animal. Implémenté par chaque sous-classe. */
  public abstract String faireDuBruit();

  /**
   * Fabrique : retourne la sous-classe d'{@code Animal} correspondant au type demandé.
   *
   * @param type "chien", "chat", "vache" ou "canard"
   * @param nom nom de l'animal
   */
  public static Animal creer(String type, String nom) {
    return switch (type) {
      case "chien" -> new Chien(nom);
      case "chat" -> new Chat(nom);
      case "vache" -> new Vache(nom);
      case "canard" -> new Canard(nom);
      default -> throw new IllegalArgumentException("Type inconnu : " + type);
    };
  }
}
// --end-solution--
/* --student--
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

  // Retourne le cri de l'animal selon son type.
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
--end-student-- */
