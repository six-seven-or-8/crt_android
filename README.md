# 📱 CRT Líneas — App Android

**Hecho en México 🇲🇽 | Made in Mexico 🇲🇽**

**Autor / Author:** Six-Seven &nbsp;·&nbsp; **Versión / Version:** 1.0.0 &nbsp;·&nbsp; **Licencia / License:** MIT
**Donaciones / Donations:** [ko-fi.com/sixseven8](https://ko-fi.com/sixseven8)

---

## 🇲🇽 El problema (o: cómo el CRT te quiere ver sufriendo, ahora en tu celular)

Ya existe la extensión de Chrome y Firefox para esto. Pero hay personas que:

- No usan computadora
- No saben instalar extensiones
- Quieren revisar desde el camión mientras aguantan los 50°C de un martes en CDMX
- O simplemente prefieren las apps

Para esas personas: esta app.

La misma lógica. El mismo sarcasmo. Ahora con pantalla táctil.

---

## 🇺🇸 The problem (mobile edition)

The Chrome/Firefox extension already exists. But some people:

- Don't use a computer
- Don't know how to install extensions
- Want to check while on the bus in 50°C Mexico City traffic
- Or just prefer apps

For those people: this app.

Same logic. Same sarcasm. Now with a touchscreen.

---

## ✨ ¿Qué hace? / What does it do?

**Consulta todos los portales oficiales del CRT de México** para verificar si hay líneas telefónicas registradas a tu nombre. Lo que sin esta app toma entre 3 y 5 horas, aquí toma entre 5 y 15 minutos.

**Queries all official CRT portals in Mexico** to check whether phone lines are registered in your name. What takes 3-5 hours without this app takes 5-15 minutes here.

### Modos de consulta / Query modes:

| Modo / Mode | Portales / Portals | Descripción / Description |
|---|---|---|
| **API directa** | 6 | Sin abrir nada. Resultado en segundos. / No browser needed. Results in seconds. |
| **WebView** | 11 + Altán (~67 OMVs) | Se abre dentro de la app con datos prellenados. / Opens inside the app with pre-filled data. |
| **Manuales** | 12 | Requieren credenciales adicionales. / Require additional credentials. |
| **Error conocido** | 15 | Caídos y monitoreados. / Down and monitored. |

---

## 🔒 Seguridad / Security

### Cifrado AES-256-GCM con Android Keystore

Tu CURP, RFC o número de pasaporte se almacena cifrado con AES-256-GCM usando el **Android Keystore System**. La clave de cifrado vive en el chip de seguridad del dispositivo y nunca sale de él. Ni nosotros podemos leerla. Ni el SAT. Ni tu ex.

```
Android Keystore System
    └── Clave AES-256-GCM (hardware-backed)
         └── EncryptedSharedPreferences
              ├── CURP/RFC/Pasaporte (cifrado)
              ├── Resultados (cifrado)
              └── Timestamp de expiración
```

**TTL 24 horas:** los datos se borran solos. Como debería ser. A diferencia del SAT.

### Data Safety (Google Play)

| Dato | Recopilado | Compartido | Cifrado | TTL |
|---|---|---|---|---|
| CURP/RFC/Pasaporte | Solo local | Nunca | AES-256-GCM | 24h |
| Resultados | Solo local | Nunca | AES-256-GCM | 24h |
| Preferencia de idioma | Solo local | Nunca | AES-256-SIV | Indefinido |

---

## 🏗️ Arquitectura / Architecture

```
mx.sixseven.crtlineas/
├── MainActivity.kt              Navegación principal
├── data/
│   ├── SecureStorage.kt         Cifrado AES-256-GCM + TTL 24h
│   ├── Companies.kt             Lista completa de portales del CRT
│   └── ApiService.kt            APIs directas: Weex, MoBig, Yo Mobile, IENTC, Sorcel
├── model/
│   └── Models.kt                UserData, Company, QueryResult, SessionState
├── ui/
│   ├── theme/Theme.kt           Paleta azul #1E3A8A / naranja #F97316 + dark mode
│   ├── splash/SplashScreen.kt   60 frases de bienvenida aleatorias
│   ├── form/FormScreen.kt       Formulario con validación en tiempo real
│   ├── progress/                Progreso de consulta (APIs paralelas + WebViews)
│   ├── results/ResultsScreen.kt Resultados + 90 frases de donación + Ko-fi
│   └── manual/                  Portales manuales
└── util/
    └── Phrases.kt               210 frases + 60 de "llevas rato aquí"
```

**Stack:**
- Kotlin + Jetpack Compose
- OkHttp (APIs directas)
- EncryptedSharedPreferences / AES-256-GCM / Android Keystore
- Coroutines (llamadas paralelas)
- Material Design 3

---

## 🚀 Instalación para desarrollo / Development setup

```bash
git clone https://github.com/six-seven/crt-lineas-android.git
cd crt-lineas-android

# Abrir en Android Studio
# Compilar con SDK 35, minSdk 26 (Android 8.0+)
# Ejecutar en emulador o dispositivo físico
```

**Requisitos:**
- Android Studio Hedgehog o superior
- SDK 35 (Android 15)
- Kotlin 2.0+
- JVM 17

---

## 📦 Build de release / Release build

```bash
./gradlew assembleRelease

# APK en: app/build/outputs/apk/release/
```

Para Play Store: usar `bundleRelease` para generar `.aab`.

---

## 🌍 Idiomas / Languages

19 idiomas disponibles. El idioma se detecta automáticamente del sistema operativo del dispositivo. Las frases de bienvenida y donación se adaptan al contexto cultural de cada idioma — no es traducción mecánica, los chistes cambian según el equivalente local del SAT en cada país.

---

## 📋 Play Store Checklist

- [x] Solo permiso `INTERNET` — cero permisos invasivos
- [x] Data Safety Form — datos locales, cifrados, sin compartir
- [x] Target API 35
- [x] minSdk 26 (cubre el 95%+ de dispositivos Android activos)
- [x] Privacy Policy URL requerida antes de publicar
- [x] Screenshots de todas las pantallas principales
- [x] Descripción en español e inglés
- [ ] Privacy Policy pública (pendiente)
- [ ] Firma de release APK
- [ ] Testing en 3+ dispositivos físicos

---

## 💰 Donaciones / Donations

Esta app es gratuita. Sin publicidad. Sin suscripción. Si las personitas diminutas que trabajan dentro de ella te fueron útiles, considera donar.

**Ko-fi:** [ko-fi.com/sixseven8](https://ko-fi.com/sixseven8)

| Cripto | Red | Dirección | Tag/Memo |
|---|---|---|---|
| XRP | Ripple | `rLSn6Z3T8uCxbcd1oxwfGQN1Fdn5CyGujK` | `11550963` |
| ADA | Cardano | `addr1q9q48dvqwgfvrw6dhwhydushnd4qnfdsqxr2gxe93wkwk09sve5ted8m0wl99677rdgqrdhslk0g2l7skx2nrklpgdeqnhsyyr` | — |
| XLM | Stellar | `GA22MHPWUODDYFSQMQ3I6BJAHEJCDLEPOIYG5RP47LLIO3YV3KPSIVXV` | `11550963` |

---

## 📜 Licencia / License

MIT — Copyright (c) 2026 Six-Seven

---

*Hecho con 💙 en México. Las personitas diminutas que trabajan dentro de la app también son mexicanas. O eso creemos. Nunca hemos podido verlas bien.*

*Made with 💙 in Mexico. The tiny little people working inside the app are also Mexican. Probably. We've never been able to see them clearly.*
