#!/bin/bash
# ============================================================
# Synchronise le dépôt local avec le repo enseignant d'origine.
#
# Détecte automatiquement le nom du TP depuis pom.xml et tire
# les dernières modifications depuis IUTInfoAix-R203/<tp>.
#
# Usage : ./sync-upstream.sh
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Détection du nom de TP depuis l'artifactId du pom.xml
TP_NAME=$(grep '<artifactId>' pom.xml | head -1 | sed 's/.*<artifactId>//' | sed 's/<.*//' | tr -d ' ')

if [ -z "$TP_NAME" ]; then
    echo "ERREUR: impossible de détecter l'artifactId dans pom.xml" >&2
    exit 1
fi

UPSTREAM="https://github.com/IUTInfoAix-R203/${TP_NAME}.git"

# Stash automatique si le working tree est dirty
STASHED=false
if ! git diff --quiet 2>/dev/null || ! git diff --cached --quiet 2>/dev/null; then
    echo "Modifications locales détectées - stash automatique..."
    git stash push -m "sync-upstream: sauvegarde avant mise à jour"
    STASHED=true
fi

echo "Synchronisation depuis ${UPSTREAM} (branche main)..."
PULL_OK=true
git pull "$UPSTREAM" main --no-rebase --no-edit --allow-unrelated-histories || PULL_OK=false

# Auto-résolution de .template-version (conflit systématique, toujours
# prendre la version upstream puisqu'on VEUT la dernière version du template)
if git diff --name-only --diff-filter=U 2>/dev/null | grep -q '\.template-version'; then
    echo "Résolution automatique du conflit sur .template-version (version upstream)..."
    git checkout --theirs .template-version
    git add .template-version
fi

# S'il reste d'autres conflits, prévenir
REMAINING_CONFLICTS=$(git diff --name-only --diff-filter=U 2>/dev/null)
if [ -n "$REMAINING_CONFLICTS" ]; then
    echo ""
    echo "⚠️  Conflits restants à résoudre manuellement :"
    echo "$REMAINING_CONFLICTS" | sed 's/^/   - /'
    echo ""
    echo "Résous-les, puis :"
    echo "  git add ."
    echo "  git commit -m \"merge: sync upstream\""
else
    # Pas de conflit (ou tous résolus) → finaliser le merge si nécessaire
    if ! $PULL_OK; then
        git commit --no-edit 2>/dev/null || true
    fi
fi

# Restauration du stash
if $STASHED; then
    echo "Restauration de tes modifications locales..."
    if git stash pop; then
        echo "✅ Tes modifications ont été ré-appliquées sans conflit."
    else
        echo "⚠️  Conflit lors du stash pop. Tes modifications sont dans le stash."
        echo "   Lance 'git stash show' pour les voir et 'git stash pop' pour réessayer."
    fi
fi

echo ""
echo "=== Synchronisation terminée ==="
