#!/bin/bash
# ══════════════════════════════════════════════════════════
# generate_keystore.sh — CRT Líneas Android
# Genera el keystore de firma y muestra los valores
# para pegar en GitHub Secrets.
#
# IMPORTANTE: Guarda el archivo keystore.jks en un lugar seguro.
# Si lo pierdes, nunca más podrás actualizar la app en Play Store.
# ══════════════════════════════════════════════════════════

set -e

ALIAS="crt-lineas-key"
KEYSTORE="crt-lineas-release.jks"
VALIDITY=10000  # ~27 años

echo ""
echo "═══════════════════════════════════════"
echo "  Generador de Keystore — CRT Líneas"
echo "═══════════════════════════════════════"
echo ""
echo "Se generará un keystore para firmar la app."
echo "Necesitas elegir dos contraseñas:"
echo "  - STORE_PASSWORD: contraseña del keystore"
echo "  - KEY_PASSWORD:   contraseña de la clave"
echo ""
echo "Puedes usar la misma para ambas (más simple)."
echo ""

read -s -p "STORE_PASSWORD (contraseña del keystore): " STORE_PASS
echo ""
read -s -p "KEY_PASSWORD (contraseña de la clave, Enter para igual a STORE_PASSWORD): " KEY_PASS
echo ""

if [ -z "$KEY_PASS" ]; then
  KEY_PASS="$STORE_PASS"
fi

echo ""
echo "Generando keystore..."

keytool -genkey -v \
  -keystore "$KEYSTORE" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity $VALIDITY \
  -storepass "$STORE_PASS" \
  -keypass "$KEY_PASS" \
  -dname "CN=Six-Seven, OU=CRT Lineas, O=Six-Seven, L=Mexico, ST=Mexico, C=MX"

echo ""
echo "✅ Keystore generado: $KEYSTORE"
echo ""
echo "═══════════════════════════════════════"
echo "  VALORES PARA GITHUB SECRETS"
echo "═══════════════════════════════════════"
echo ""
echo "Ve a: GitHub repo → Settings → Secrets and variables → Actions → New repository secret"
echo ""
echo "Agrega estos 4 secrets:"
echo ""
echo "Secret 1 — KEYSTORE_BASE64:"
echo "  (copia TODO el texto base64 que aparece abajo)"
echo ""
base64 "$KEYSTORE"
echo ""
echo "Secret 2 — KEY_ALIAS:"
echo "  $ALIAS"
echo ""
echo "Secret 3 — KEY_PASSWORD:"
echo "  $KEY_PASS"
echo ""
echo "Secret 4 — STORE_PASSWORD:"
echo "  $STORE_PASS"
echo ""
echo "═══════════════════════════════════════"
echo "  ⚠️  GUARDA ESTE ARCHIVO"
echo "═══════════════════════════════════════"
echo ""
echo "Guarda $KEYSTORE en un lugar seguro (Dropbox, Drive, USB)."
echo "Si lo pierdes NO puedes actualizar la app en Play Store."
echo ""
