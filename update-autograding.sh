#!/bin/bash
# ============================================================
# Régénère la section AUTOGRADING de .github/workflows/classroom.yml
# en scannant les paquets d'exercices dans src/test/java/fr/univ_amu/iut/
#
# Utilise les actions GitHub Classroom modernes (forkées et maintenues
# côté R202, réutilisées par R203 - infrastructure partagée) :
#   - IUTInfoAix-R202/autograding-command-grader@main
#   - IUTInfoAix-R202/autograding-grading-reporter@main
#
# Granularité : un step de grading PAR MÉTHODE de test (et non par
# exercice). Cela permet une vraie note proportionnelle :
# si 1 test sur 3 d'un exercice passe, l'élève reçoit 1/3 des points
# de cet exercice.
#
# La commande de grading appelle le wrapper ./grade-test.sh qui
# exige que le test ait RÉELLEMENT tourné (pas @Disabled) ET passé.
# Sans ce wrapper, un TP vide aurait 100/100 car ./mvnw test sur un
# test @Disabled exit 0 (Skipped, pas Failed).
#
# Convention : un sous-paquet `exerciceN` = un exercice.
#
# Répartition sur un total de 1000 :
#   - 100 pts compilation
#   - 900 pts équirépartis entre les exercices détectés (les
#     $ex_remainder premiers exercices prennent +1 pt pour absorber
#     le reste, pas de "winner-takes-all").
#   - À l'intérieur d'un exercice, les points sont équirépartis
#     entre ses méthodes de test (même règle : les $m_remainder
#     premières absorbent +1). Le total sur 1000 offre assez de
#     granularité pour qu'aucun test ne vale 0 pt, même sur les
#     exercices à forte cardinalité de tests.
#
# Le reporter GitHub Classroom affiche le score brut (ex :
# "Points 250/1000"). Les READMEs doivent donc documenter la base
# 1000 explicitement, ou la présenter en pourcentage (25 %).
#
# Usage: ./update-autograding.sh
# ============================================================

set -e

TEST_ROOT="src/test/java/fr/univ_amu/iut"
CLASSROOM_YML=".github/workflows/classroom.yml"
COMPILE_POINTS=100
TOTAL_EXERCISE_POINTS=900
TIMEOUT_MINUTES=5
TEST_PACKAGE_PREFIX="fr.univ_amu.iut"

START_MARKER="#@@@AUTOGRADING-BEGIN@@@"
END_MARKER="#@@@AUTOGRADING-END@@@"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "$CLASSROOM_YML" ]; then
    echo "ERREUR: $CLASSROOM_YML introuvable." >&2
    exit 1
fi

# Helper : extrait les noms de méthodes @Test d'un fichier de test.
# On ne prend que les méthodes annotées @Test (pas @BeforeEach, @Start,
# ni utilitaires void). L'annotation peut être sur la ligne au-dessus
# ou sur la même ligne que la signature.
extract_test_methods() {
    local file=$1
    awk '
        /@Test([^a-zA-Z0-9_]|$)/ { pending = 1; next }
        pending && /void[[:space:]]+[a-zA-Z_][a-zA-Z0-9_]*[[:space:]]*\(/ {
            match($0, /void[[:space:]]+[a-zA-Z_][a-zA-Z0-9_]*/)
            name = substr($0, RSTART, RLENGTH)
            sub(/^void[[:space:]]+/, "", name)
            print name
            pending = 0
            next
        }
        pending && /^[[:space:]]*$/ { next }
        pending && /^[[:space:]]*@/ { next }
        { pending = 0 }
    ' "$file" 2>/dev/null || true
}

# --- Découverte des exercices ---
exercises=()
if [ -d "$TEST_ROOT" ]; then
    while IFS= read -r dir; do
        exercises+=("$(basename "$dir")")
    done < <(find "$TEST_ROOT" -mindepth 1 -maxdepth 1 -type d -name 'exercice*' | sort -V)
fi

num_ex=${#exercises[@]}
echo "Exercices détectés : $num_ex"

# --- Répartition des points entre exercices ---
if [ "$num_ex" -eq 0 ]; then
    compile_points=1000
else
    compile_points=$COMPILE_POINTS
    ex_base=$(( TOTAL_EXERCISE_POINTS / num_ex ))
    ex_remainder=$(( TOTAL_EXERCISE_POINTS - ex_base * num_ex ))
fi

# --- Génération du bloc YAML ---
block=$(mktemp)
trap 'rm -f "$block"' EXIT

{
    echo "      ${START_MARKER} (généré par update-autograding.sh, ne pas éditer à la main)"
    echo "      - name: Compilation"
    echo "        id: compilation"
    echo "        uses: IUTInfoAix-R202/autograding-command-grader@main"
    echo "        with:"
    echo "          test-name: Compilation"
    echo "          setup-command: \"\""
    echo "          command: ./mvnw -B -q compile"
    echo "          timeout: ${TIMEOUT_MINUTES}"
    echo "          max-score: ${compile_points}"

    runners="compilation"
    env_block="          COMPILATION_RESULTS: \"\${{ steps.compilation.outputs.result }}\""

    for i in "${!exercises[@]}"; do
        ex_name="${exercises[$i]}"
        # Points de cet exercice : les $ex_remainder premiers prennent +1 pt
        # pour absorber le reste, pas de "winner-takes-all" sur le dernier.
        if [ "$i" -lt "$ex_remainder" ]; then
            ex_points=$(( ex_base + 1 ))
        else
            ex_points=$ex_base
        fi

        # Découverte des méthodes de test de cet exercice
        ex_dir="$TEST_ROOT/$ex_name"
        method_count=0
        # Tableaux locaux à cet exercice : (FQCN classe, nom méthode)
        unset ex_classes ex_methods
        ex_classes=()
        ex_methods=()

        while IFS= read -r f; do
            class_name=$(basename "$f" .java)
            while IFS= read -r m; do
                [ -z "$m" ] && continue
                ex_classes+=("${TEST_PACKAGE_PREFIX}.${ex_name}.${class_name}")
                ex_methods+=("$m")
                method_count=$((method_count + 1))
            done < <(extract_test_methods "$f")
        done < <(find "$ex_dir" -type f -name '*.java' 2>/dev/null | sort)

        if [ "$method_count" -eq 0 ]; then
            echo "  - $ex_name : aucune méthode @Test trouvée, ignoré (${ex_points} pts perdus)" >&2
            continue
        fi

        echo "  - $ex_name : $method_count méthode(s) ($ex_points pts)" >&2

        # Répartition des points entre méthodes : les $m_remainder
        # premières méthodes prennent chacune +1 pt pour absorber le reste,
        # au lieu de tout donner à la dernière. La base à 1000 (au lieu
        # de 100) garantit m_base >= 1 dans tous les cas réalistes, donc
        # aucun test ne vaut 0 (ex : 180 pts / 27 tests -> 18 tests à 7 pts
        # + 9 tests à 6 pts).
        m_base=$(( ex_points / method_count ))
        m_remainder=$(( ex_points - m_base * method_count ))

        for j in "${!ex_methods[@]}"; do
            method="${ex_methods[$j]}"
            fqcn="${ex_classes[$j]}"
            if [ "$j" -lt "$m_remainder" ]; then
                m_points=$(( m_base + 1 ))
            else
                m_points=$m_base
            fi

            step_id="${ex_name}_${method}"
            env_var_name=$(echo "$step_id" | tr '[:lower:]-' '[:upper:]_')

            cmd="./grade-test.sh ${fqcn} ${method}"

            echo ""
            echo "      - name: \"${ex_name} : ${method}\""
            echo "        id: ${step_id}"
            echo "        uses: IUTInfoAix-R202/autograding-command-grader@main"
            echo "        with:"
            echo "          test-name: \"${ex_name} : ${method}\""
            echo "          setup-command: \"\""
            echo "          command: ${cmd}"
            echo "          timeout: ${TIMEOUT_MINUTES}"
            echo "          max-score: ${m_points}"

            runners="${runners},${step_id}"
            env_block="${env_block}"$'\n'"          ${env_var_name}_RESULTS: \"\${{ steps.${step_id}.outputs.result }}\""
        done
    done

    echo ""
    echo "      - name: Autograding Reporter"
    echo "        uses: IUTInfoAix-R202/autograding-grading-reporter@main"
    # continue-on-error absorbe l'exit != 0 du reporter quand le score est
    # partiel. Le score reste publié (annotation + check runs), mais le
    # workflow global reste vert : un TP en cours ne donne plus un "CI
    # cassé". Les vrais problèmes techniques (compilation, tests individuels)
    # restent rouges car leurs steps n'ont PAS continue-on-error.
    echo "        continue-on-error: true"
    echo "        env:"
    echo "${env_block}"
    echo "        with:"
    echo "          runners: ${runners}"
    echo "      ${END_MARKER}"
} > "$block"

# --- Splice dans classroom.yml entre les marqueurs ---
start_line=$(grep -n "$START_MARKER" "$CLASSROOM_YML" | head -1 | cut -d: -f1)
end_line=$(grep -n "$END_MARKER" "$CLASSROOM_YML" | head -1 | cut -d: -f1)

if [ -z "$start_line" ] || [ -z "$end_line" ]; then
    echo "ERREUR: marqueurs AUTOGRADING absents de ${CLASSROOM_YML}" >&2
    exit 1
fi

{
    head -n "$((start_line - 1))" "$CLASSROOM_YML"
    cat "$block"
    tail -n +"$((end_line + 1))" "$CLASSROOM_YML"
} > "${CLASSROOM_YML}.new"
mv "${CLASSROOM_YML}.new" "$CLASSROOM_YML"

echo ""
echo "=> ${CLASSROOM_YML} mis à jour."
