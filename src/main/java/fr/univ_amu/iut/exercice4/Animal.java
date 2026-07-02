package fr.univ_amu.iut.exercice4;

/// Exercice 4 - Replace Conditional with Polymorphism.
///
/// Cette classe `Animal` a un champ `type` (une [String]...) et une méthode [#faireDuBruit()]
/// qui est un gros `switch` sur ce type. Chaque fois qu'on ajoute une espèce, il faut modifier le
/// switch - et risque d'oublier un cas.
///
/// Smells présents :
///
/// - **Type Code** : le champ `type` est un String alors qu'il représente une énumération fermée
///   (chien, chat, vache, canard)
/// - **Switch Statements** : le switch duplique la logique "qui fait quel bruit"
/// - **Shotgun Surgery** : ajouter une nouvelle espèce demande de modifier potentiellement
///   plusieurs méthodes (ici uniquement `faireDuBruit()`, mais imaginez si on ajoute `nourrir()`,
///   `dormir()`, etc.)
///
/// Refactoring attendu : **Replace Conditional with Polymorphism**. Transformer `Animal` en
/// classe abstraite, créer `Chien`, `Chat`, `Vache`, `Canard` qui héritent et redéfinissent
/// `faireDuBruit()`. Le switch disparaît ; la JVM s'occupe du dispatch. On garde un point d'entrée
/// `Animal.creer(type, nom)` qui retourne la bonne sous-classe, pour les appelants qui ne
/// connaissent que le type sous forme de String.
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
