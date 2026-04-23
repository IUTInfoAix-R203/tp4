#!/bin/bash
# ============================================================
# Génère la version étudiante d'un TP à partir de la branche
# solution.
#
# La branche "solution" contient les implémentations complètes
# avec des marqueurs :
#   // --solution--
#   code à supprimer...
#   // --end-solution--
#
# Le script :
#   1. Copie la branche solution dans un répertoire temporaire
#   2. Supprime les blocs entre marqueurs (source)
#   3. Ajoute @Disabled aux tests des exercices
#   4. Lance Spotless (supprime imports inutilisés, reformate)
#   5. Vérifie que la version étudiante compile
#   6. Copie le résultat dans le TP cible (si --apply)
#
# Mode par défaut : DRY-RUN (affiche les fichiers qui seraient
# modifiés sans rien écrire). Ajoute --apply pour appliquer.
#
# Usage :
#   ./generate-student.sh <tp_dir>                # dry-run
#   ./generate-student.sh --apply <tp_dir>        # applique
#
# Exemples :
#   ./generate-student.sh ../tp1
#   ./generate-student.sh --apply ../tp1
#
# Prérequis : le TP doit avoir une branche "solution" et un
# worktree propre pour --apply. Le script ne modifie jamais la
# branche solution.
# ============================================================

set -euo pipefail

# --- Couleurs (si terminal) ---
if [ -t 1 ]; then
    GREEN='\033[0;32m'
    YELLOW='\033[0;33m'
    RED='\033[0;31m'
    CYAN='\033[0;36m'
    BOLD='\033[1m'
    NC='\033[0m'
else
    GREEN='' YELLOW='' RED='' CYAN='' BOLD='' NC=''
fi

info()  { echo -e "${CYAN}▸${NC} $*"; }
ok()    { echo -e "${GREEN}✓${NC} $*"; }
warn()  { echo -e "${YELLOW}⚠${NC} $*"; }
fail()  { echo -e "${RED}✗${NC} $*" >&2; exit 1; }

ensure_clean_worktree() {
    local status
    status=$(git -C "$TP_DIR" status --short --untracked-files=all)
    if [ -n "$status" ]; then
        fail "Le worktree de $TP_DIR n'est pas propre. Commit/stash/review manuellement avant --apply."
    fi
}

insert_disabled_import() {
    local file=$1
    local insert_after

    if grep -q '^import org\.junit\.jupiter\.api\.Disabled;$' "$file"; then
        return 0
    fi

    insert_after=$(grep -n '^import org\.junit\.jupiter\.api\.' "$file" | tail -1 | cut -d: -f1 || true)
    if [ -z "$insert_after" ]; then
        insert_after=$(grep -n '^import ' "$file" | head -1 | cut -d: -f1 || true)
    fi
    if [ -z "$insert_after" ]; then
        insert_after=$(grep -n '^package ' "$file" | head -1 | cut -d: -f1 || true)
    fi
    [ -n "$insert_after" ] || fail "Impossible d'insérer l'import Disabled dans $file"

    awk -v line_no="$insert_after" '
    NR == line_no {
        print
        print "import org.junit.jupiter.api.Disabled;"
        next
    }
    { print }
    ' "$file" > "${file}.tmp" && mv "${file}.tmp" "$file"
}

disable_exercise_tests_in_file() {
    local file=$1

    awk -v disabled="$DISABLED_TEXT" '
    function flush_annotations(    i, has_test, has_disabled, indent) {
        if (annotation_count == 0) {
            return
        }

        has_test = 0
        has_disabled = 0
        for (i = 1; i <= annotation_count; i++) {
            if (annotation_lines[i] ~ /^[[:space:]]*@Disabled(\(|$)/) {
                has_disabled = 1
            }
            if (annotation_lines[i] ~ /^[[:space:]]*@(Test|ParameterizedTest|RepeatedTest|TestFactory|TestTemplate)(\(|[[:space:]]|$)/) {
                has_test = 1
            }
        }

        if (has_test && !has_disabled) {
            match(annotation_lines[1], /^[[:space:]]*/)
            indent = substr(annotation_lines[1], RSTART, RLENGTH)
            print indent "@Disabled(\"" disabled "\")"
        }

        for (i = 1; i <= annotation_count; i++) {
            print annotation_lines[i]
            delete annotation_lines[i]
        }
        annotation_count = 0
    }

    /^[[:space:]]*@/ {
        annotation_lines[++annotation_count] = $0
        next
    }

    {
        flush_annotations()
        print $0
    }

    END {
        flush_annotations()
    }
    ' "$file" > "${file}.tmp" && mv "${file}.tmp" "$file"

    if grep -q '@Disabled("' "$file"; then
        insert_disabled_import "$file"
    fi
}

checkout_main_branch() {
    if git -C "$TP_DIR" checkout main -q 2>/dev/null; then
        return 0
    fi

    if git -C "$TP_DIR" show-ref --verify --quiet refs/remotes/origin/main; then
        git -C "$TP_DIR" checkout -B main origin/main -q
        return 0
    fi

    fail "Impossible de basculer sur la branche main dans $TP_DIR"
}

# --- Arguments ---
APPLY=false
if [ "${1:-}" = "--apply" ]; then
    APPLY=true
    shift
fi

if [ $# -lt 1 ]; then
    echo "Usage: $0 [--apply] <tp_dir>"
    exit 1
fi

TP_DIR="$(cd "$1" && pwd)"

# --- Validation ---
[ -f "$TP_DIR/pom.xml" ] || fail "$TP_DIR ne contient pas de pom.xml"
[ -d "$TP_DIR/.git" ]    || fail "$TP_DIR n'est pas un dépôt git"

git -C "$TP_DIR" rev-parse --verify solution >/dev/null 2>&1 \
    || fail "La branche 'solution' n'existe pas dans $TP_DIR"

DISABLED_TEXT='Retire cette annotation pour activer le test'

if [ "$APPLY" = true ]; then
    ensure_clean_worktree
fi

# --- Répertoire temporaire ---
TMP_DIR=$(mktemp -d)
KEEP_TMP_DIR=false
cleanup() {
    if [ "$KEEP_TMP_DIR" = false ]; then
        rm -rf "$TMP_DIR"
    else
        warn "Répertoire temporaire conservé pour inspection : $TMP_DIR"
    fi
}
trap cleanup EXIT

info "Extraction de la branche solution..."
git -C "$TP_DIR" archive solution | tar -C "$TMP_DIR" -x

# --- 1. Strip des blocs solution (source) ---
info "Suppression des blocs // --solution-- ... // --end-solution--"

STRIPPED=0
while IFS= read -r -d '' file; do
    if grep -q '// --solution--' "$file"; then
        sed -i '/\/\/ --solution--/,/\/\/ --end-solution--/d' "$file"
        STRIPPED=$((STRIPPED + 1))
        echo "  stripped: ${file#"$TMP_DIR"/}"
    fi
done < <(find "$TMP_DIR/src/main/java" \( -path '*/exercice*/*.java' -o -path '*/bonus*/*.java' \) -print0 2>/dev/null)

ok "$STRIPPED fichier(s) source strippé(s)"

# --- 2. Ajout de @Disabled aux tests ---
info "Ajout de @Disabled aux tests des exercices..."

DISABLED_COUNT=0
while IFS= read -r -d '' file; do
    disable_exercise_tests_in_file "$file"

    ADDED=$(grep -c "@Disabled" "$file" || true)
    if [ "$ADDED" -gt 0 ]; then
        DISABLED_COUNT=$((DISABLED_COUNT + ADDED))
        echo "  @Disabled: ${file#"$TMP_DIR"/} ($ADDED tests)"
    fi
done < <(find "$TMP_DIR/src/test/java" \( -path '*/exercice*/*.java' -o -path '*/bonus*/*.java' \) -print0 2>/dev/null)

ok "$DISABLED_COUNT annotation(s) @Disabled ajoutée(s)"

# --- 3. Spotless (supprime imports inutilisés, reformate) ---
info "Exécution de Spotless (formatage + nettoyage imports)..."

if [ -f "$TMP_DIR/mvnw" ]; then
    chmod +x "$TMP_DIR/mvnw" 2>/dev/null || true
fi

# Initialiser un repo git temporaire (le plugin git-build-hook en a besoin)
git -C "$TMP_DIR" init -q 2>/dev/null || true

(cd "$TMP_DIR" && ./mvnw -B -q spotless:apply 2>&1) \
    || warn "Spotless a renvoyé une erreur (non bloquant)"

ok "Spotless terminé"

# --- 4. Vérification compilation ---
info "Vérification de la compilation..."

(cd "$TMP_DIR" && ./mvnw -B -q compile 2>&1) \
    || {
        KEEP_TMP_DIR=true
        fail "La version étudiante ne compile pas."
    }

ok "Compilation réussie"

# --- 5. Vérification rapide : tests skipped ---
info "Vérification que les tests sont bien @Disabled..."

TEST_OUTPUT=$(cd "$TMP_DIR" && ./mvnw -B -q test 2>&1 || true)
SKIPPED=$(echo "$TEST_OUTPUT" | grep -oP 'Skipped: \K[0-9]+' | tail -1 || true)
TOTAL=$(echo "$TEST_OUTPUT" | grep -oP 'Tests run: \K[0-9]+' | tail -1 || true)

if [ -n "$SKIPPED" ] && [ -n "$TOTAL" ]; then
    ok "Tests : $TOTAL exécutés, $SKIPPED skippés"
else
    warn "Impossible de parser le résultat des tests"
fi

# --- 6. Application ou dry-run ---
if [ "$APPLY" = true ]; then
    info "Application des modifications sur $TP_DIR..."

    CURRENT_BRANCH=$(git -C "$TP_DIR" branch --show-current)
    if [ "$CURRENT_BRANCH" != "main" ]; then
        info "Basculement sur la branche main..."
        checkout_main_branch
    fi

    rsync -a --delete \
        --exclude='.git' \
        --exclude='target' \
        --exclude='generate-student.sh' \
        --exclude='update-autograding.sh' \
        --exclude='.github/workflows/generate-student.yml' \
        --exclude='.github/workflows/template-sync.yml' \
        "$TMP_DIR/" "$TP_DIR/"

    ok "Version étudiante générée dans $TP_DIR/"
    echo ""
    echo -e "${BOLD}Prochaines étapes :${NC}"
    echo "  cd $TP_DIR"
    echo "  git diff                    # reviewer les changements"
    echo "  git add -A"
    echo "  ALLOW_MAIN_COMMIT=1 git commit -m \"chore: régénère version étudiante depuis solution\""
else
    echo ""
    echo -e "${BOLD}=== DRY-RUN ===${NC}"
    echo "Aucune modification n'a été appliquée."
    echo ""

    if command -v diff >/dev/null 2>&1; then
        DIFF_OUTPUT=$(diff -rq \
            --exclude='.git' --exclude='target' \
            --exclude='generate-student.sh' \
            --exclude='update-autograding.sh' \
            --exclude='generate-student.yml' \
            --exclude='template-sync.yml' \
            "$TMP_DIR" "$TP_DIR" 2>/dev/null || true)
        DIFF_COUNT=$(echo "$DIFF_OUTPUT" | grep -c '.' || true)
        echo "$DIFF_COUNT fichier(s) différent(s) entre solution strippée et main actuel."
        echo ""
        echo "$DIFF_OUTPUT"
    fi

    echo ""
    echo "Pour appliquer : $0 --apply $1"
fi
