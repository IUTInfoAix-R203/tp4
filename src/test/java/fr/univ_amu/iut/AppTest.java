package fr.univ_amu.iut;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;

/**
 * Smoke test du lanceur : vérifie que le menu affiche bien le titre du TP et le repère "IUT
 * Aix-Marseille" sur l'entrée standard. Ce test sert de garde-fou : il échoue si quelqu'un casse la
 * classe {@link App} au point qu'elle ne compile plus ou n'imprime plus son en-tête.
 */
class AppTest {

  @Test
  void le_menu_affiche_le_titre_du_tp_et_l_iut() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(buffer));
      App.afficherMenu(new LinkedHashMap<>());
    } finally {
      System.setOut(originalOut);
    }
    assertThat(buffer.toString()).contains("TP4").contains("IUT Aix-Marseille");
  }
}
