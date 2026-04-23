package fr.univ_amu.iut.exercice5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ServiceNotificationTest {

  private final ServiceNotification service = new ServiceNotification();

  // =========================================================================
  // Caractérisation
  // =========================================================================

  @Test
  void mailMinimal_sansImportanceSansPJ() {
    String mail = service.envoyer("a@b.c", "c@d.e", "Bonjour", "Hello", false, 3, null);
    assertThat(mail)
        .contains("De: c@d.e")
        .contains("A: a@b.c")
        .contains("Sujet: Bonjour")
        .contains("Corps: Hello")
        .contains("[P3]");
    assertThat(mail).doesNotContain("[IMPORTANT]");
  }

  @Test
  void mailImportantAfficheLeTag() {
    String mail = service.envoyer("a@b.c", "c@d.e", "Urgent", "Lis moi", true, 1, null);
    assertThat(mail).startsWith("[IMPORTANT]").contains("[P1]");
  }

  @Test
  void piecesJointesSontListees() {
    String mail =
        service.envoyer(
            "a@b.c", "c@d.e", "CV", "Voici", false, 2, new String[] {"cv.pdf", "lettre.pdf"});
    assertThat(mail).contains("Pieces jointes: cv.pdf, lettre.pdf");
  }

  @Test
  void sansPiecesJointes_pasDeLigneDePieces() {
    String mail = service.envoyer("a@b.c", "c@d.e", "S", "C", false, 5, new String[] {});
    assertThat(mail).doesNotContain("Pieces jointes");
  }

  // =========================================================================
  // Structure : Parameter Object MessageEmail
  // =========================================================================

  @Disabled("Activer après avoir créé MessageEmail (record)")
  @Test
  void messageEmailEstUnRecord() throws Exception {
    Class<?> cls = Class.forName("fr.univ_amu.iut.exercice3.MessageEmail");
    assertThat(cls.isRecord()).as("MessageEmail doit être un record").isTrue();
  }

  @Disabled("Activer après avoir créé MessageEmail avec ses 7 composants")
  @Test
  void messageEmailA7Composants() throws Exception {
    Class<?> cls = Class.forName("fr.univ_amu.iut.exercice3.MessageEmail");
    assertThat(cls.getRecordComponents())
        .as(
            "MessageEmail doit avoir 7 composants : destinataire, expediteur, sujet, "
                + "corps, important, priorite, piecesJointes")
        .hasSize(7);
  }

  @Disabled("Activer après avoir créé la nouvelle méthode envoyer(MessageEmail)")
  @Test
  void nouvelleSignatureAvecMessageEmail() throws Exception {
    Class<?> messageClass = Class.forName("fr.univ_amu.iut.exercice3.MessageEmail");
    java.lang.reflect.Method envoyer =
        ServiceNotification.class.getDeclaredMethod("envoyer", messageClass);
    assertThat(envoyer.getParameterCount()).isEqualTo(1);
  }

  @Disabled("Activer après avoir creé la nouvelle methode : elle doit produire le même format")
  @Test
  void nouvelleMethodeProduitLeMemeFormat() throws Exception {
    Class<?> messageClass = Class.forName("fr.univ_amu.iut.exercice3.MessageEmail");
    Object message =
        messageClass
            .getDeclaredConstructor(
                String.class,
                String.class,
                String.class,
                String.class,
                boolean.class,
                int.class,
                String[].class)
            .newInstance("a@b.c", "c@d.e", "Test", "Contenu", true, 2, new String[] {"doc.pdf"});
    java.lang.reflect.Method envoyer =
        ServiceNotification.class.getDeclaredMethod("envoyer", messageClass);
    String mailNouveau = (String) envoyer.invoke(service, message);
    String mailAncien =
        service.envoyer("a@b.c", "c@d.e", "Test", "Contenu", true, 2, new String[] {"doc.pdf"});
    assertThat(mailNouveau).isEqualTo(mailAncien);
  }
}
