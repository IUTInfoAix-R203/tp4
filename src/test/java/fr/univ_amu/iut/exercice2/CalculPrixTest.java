package fr.univ_amu.iut.exercice2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de l'exercice 2 : CalculPrix (Replace Magic Number).
 *
 * <p>Les tests sont rangés en deux catégories :
 *
 * <ul>
 *   <li>Tests de <b>caractérisation</b> : ils passent <em>dès le début</em> sur le code smelly et
 *       permettent de refactorer sans crainte. Ils décrivent précisément le comportement actuel
 *       dans chaque branche (sous/sur le seuil fidélité, sous/sur le seuil frais de port).
 *   <li>Tests de <b>structure</b> : ils vérifient par <em>réflexion</em> que les cinq constantes
 *       ont bien été nommées avec les visibilités/valeurs attendues. Ils échouent tant que le
 *       refactoring n'est pas fait.
 * </ul>
 */
class CalculPrixTest {

  // =========================================================================
  // Tests de caractérisation (actifs dès le début, doivent rester verts)
  // =========================================================================

  @Test
  void un_montant_nul_retourne_les_frais_de_port() {
    // 0 * 1.20 = 0 TTC, en dessous du seuil 50 -> +8 port = 8
    assertThat(new CalculPrix().calculerPrixFinal(0.0, false)).isEqualTo(8.0, within(0.001));
  }

  @Test
  void un_petit_montant_sous_le_seuil_des_frais_de_port_ajoute_les_frais_de_port() {
    // 30 HT * 1.20 = 36 TTC < 50 -> +8 port = 44
    assertThat(new CalculPrix().calculerPrixFinal(30.0, false)).isEqualTo(44.0, within(0.001));
  }

  @Test
  void un_montant_au_seuil_des_frais_de_port_offre_le_port() {
    // 50 HT * 1.20 = 60 TTC >= 50 -> port offert
    assertThat(new CalculPrix().calculerPrixFinal(50.0, false)).isEqualTo(60.0, within(0.001));
  }

  @Test
  void un_montant_moyen_d_un_client_non_fidele_n_applique_ni_remise_ni_frais_de_port() {
    // 200 HT * 1.20 = 240 TTC, pas fidèle -> pas de remise, port offert
    assertThat(new CalculPrix().calculerPrixFinal(200.0, false)).isEqualTo(240.0, within(0.001));
  }

  @Test
  void un_gros_montant_d_un_client_non_fidele_n_applique_pas_de_remise() {
    // 1000 HT * 1.20 = 1200 TTC, pas fidèle -> pas de remise
    assertThat(new CalculPrix().calculerPrixFinal(1000.0, false)).isEqualTo(1200.0, within(0.001));
  }

  @Test
  void un_client_fidele_sous_le_seuil_de_remise_n_applique_pas_de_remise() {
    // 400 HT * 1.20 = 480 TTC, fidèle mais TTC NON > 500 -> pas de remise
    assertThat(new CalculPrix().calculerPrixFinal(400.0, true)).isEqualTo(480.0, within(0.001));
  }

  @Test
  void un_client_fidele_sur_le_seuil_de_remise_applique_la_remise() {
    // 500 HT * 1.20 = 600 TTC > 500, fidèle -> remise -> 600 * 0.95 = 570
    assertThat(new CalculPrix().calculerPrixFinal(500.0, true)).isEqualTo(570.0, within(0.001));
  }

  @Test
  void un_client_fidele_avec_un_gros_montant_applique_la_remise() {
    // 1000 HT * 1.20 = 1200 > 500, fidèle -> 1200 * 0.95 = 1140
    assertThat(new CalculPrix().calculerPrixFinal(1000.0, true)).isEqualTo(1140.0, within(0.001));
  }

  // =========================================================================
  // Tests de structure (à activer au fur et à mesure du refactoring)
  // =========================================================================

  @Disabled("Activer après avoir extrait TAUX_TVA")
  @Test
  void la_constante_taux_tva_a_ete_extraite() throws Exception {
    assertConstanteDouble("TAUX_TVA", 1.20);
  }

  @Disabled("Activer après avoir extrait SEUIL_REMISE_FIDELITE")
  @Test
  void la_constante_seuil_remise_a_ete_extraite() throws Exception {
    assertConstanteDouble("SEUIL_REMISE_FIDELITE", 500.0);
  }

  @Disabled("Activer après avoir extrait TAUX_REMISE_FIDELITE")
  @Test
  void la_constante_taux_remise_a_ete_extraite() throws Exception {
    assertConstanteDouble("TAUX_REMISE_FIDELITE", 0.95);
  }

  @Disabled("Activer après avoir extrait SEUIL_FRAIS_PORT_OFFERT")
  @Test
  void la_constante_seuil_frais_port_a_ete_extraite() throws Exception {
    assertConstanteDouble("SEUIL_FRAIS_PORT_OFFERT", 50.0);
  }

  @Disabled("Activer après avoir extrait MONTANT_FRAIS_PORT")
  @Test
  void la_constante_montant_frais_port_a_ete_extraite() throws Exception {
    assertConstanteDouble("MONTANT_FRAIS_PORT", 8.0);
  }

  // ------------------------------------------------------------------------
  //  Helpers
  // ------------------------------------------------------------------------

  private static void assertConstanteDouble(String nom, double valeurAttendue) throws Exception {
    Field f = CalculPrix.class.getDeclaredField(nom);
    f.setAccessible(true);
    int m = f.getModifiers();
    assertThat(Modifier.isStatic(m)).as("%s doit être static", nom).isTrue();
    assertThat(Modifier.isFinal(m)).as("%s doit être final", nom).isTrue();
    assertThat(f.getDouble(null))
        .as("%s doit valoir %s", nom, valeurAttendue)
        .isEqualTo(valeurAttendue, within(0.001));
  }

  @SuppressWarnings("unused")
  private static final Class<?> DISABLED_USAGE_MARKER = Disabled.class;
}
