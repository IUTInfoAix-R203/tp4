#!/bin/bash
# ============================================================
# Helper d'autograding : lance un test Maven ciblé et vérifie
# qu'il s'est RÉELLEMENT exécuté (pas @Disabled) ET qu'il a passé.
#
# Usage : ./grade-test.sh <FQCN> <method>
#   FQCN   : nom complet de la classe de test
#            (ex: fr.univ_amu.iut.exercice1.HelloWorldTest)
#   method : nom de la méthode de test
#            (ex: retourneLeMessageAttendu)
#
# Codes de sortie :
#   0   test a été exécuté et a passé
#   != 0  test est @Disabled, a échoué, a erroré, ou est absent
#
# Pourquoi ce wrapper ?
# ---------------------
# `./mvnw test -Dtest='Class#method'` exit 0 même quand la méthode
# ciblée est @Disabled (Surefire la compte en "skipped" mais le
# build reste SUCCESS). Si on se contente de ça, un TP vide avec
# tous les tests @Disabled recevrait 100/100 au grader Classroom.
# On parse donc le rapport XML de Surefire pour exiger tests > 0
# ET skipped/failures/errors tous à 0.
# ============================================================

set -e

fqcn=$1
method=$2

if [ -z "$fqcn" ] || [ -z "$method" ]; then
    echo "Usage: $0 <FQCN> <method>" >&2
    exit 2
fi

xml="target/surefire-reports/TEST-${fqcn}.xml"

# Lance le test ; exit != 0 si Maven échoue (test failed, class not found, etc.)
./mvnw -B -q test -Dtest="${fqcn}#${method}" || exit 1

if [ ! -f "$xml" ]; then
    echo "Rapport XML absent : $xml" >&2
    exit 1
fi

# Extrait les attributs du <testsuite> racine
tests=$(grep -oE 'tests="[0-9]+"' "$xml" | head -1 | grep -oE '[0-9]+' || echo 0)
skipped=$(grep -oE 'skipped="[0-9]+"' "$xml" | head -1 | grep -oE '[0-9]+' || echo 0)
failures=$(grep -oE 'failures="[0-9]+"' "$xml" | head -1 | grep -oE '[0-9]+' || echo 0)
errors=$(grep -oE 'errors="[0-9]+"' "$xml" | head -1 | grep -oE '[0-9]+' || echo 0)

if [ "$tests" -eq 0 ] \
    || [ "$skipped" -gt 0 ] \
    || [ "$failures" -gt 0 ] \
    || [ "$errors" -gt 0 ]; then
    exit 1
fi

exit 0
