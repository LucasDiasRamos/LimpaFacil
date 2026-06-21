#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_ROOT="$ROOT_DIR/src/main/java/br/com/limpafacil"
RESOURCES_ROOT="$ROOT_DIR/src/main/resources/br/com/limpafacil"

fail() {
    echo "MVC architecture check failed: $1" >&2
    exit 1
}

for legacy_dir in service dao config util; do
    if [ -d "$JAVA_ROOT/$legacy_dir" ]; then
        fail "legacy root package still exists: br.com.limpafacil.$legacy_dir"
    fi
done

while IFS= read -r package_dir; do
    package_name="$(basename "$package_dir")"
    case "$package_name" in
        controller|model|view)
            ;;
        *)
            fail "unexpected root package found: br.com.limpafacil.$package_name"
            ;;
    esac
done < <(find "$JAVA_ROOT" -mindepth 1 -maxdepth 1 -type d)

if find "$JAVA_ROOT/controller" -type f -name '*.java' ! -name 'ControladorNavegacao.java' | grep -q .; then
    fail "controller package must contain only ControladorNavegacao.java"
fi

if rg -n 'package br\.com\.limpafacil\.(service|dao|config|util);' "$JAVA_ROOT"; then
    fail "legacy root package declaration found"
fi

if rg -n 'package br\.com\.limpafacil\.model;' "$JAVA_ROOT/model"; then
    fail "entities must use br.com.limpafacil.model.entity"
fi

if rg -n '^import javafx\.' "$JAVA_ROOT/model"; then
    fail "model must not import JavaFX"
fi

if rg -n 'fx:controller="br\.com\.limpafacil\.controller\.' "$RESOURCES_ROOT/view"; then
    fail "FXML controllers must be in br.com.limpafacil.view"
fi

echo "MVC architecture check passed."
