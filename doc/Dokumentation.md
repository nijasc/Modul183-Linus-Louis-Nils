# Linkstack – Projektdokumentation

**Modul 183 – Applikationssicherheit implementieren**  
**Autoren:** Linus, Louis, Nils  
**Datum:** 2026-05-31

---

## Inhaltsverzeichnis

1. [Projektübersicht](#1-projektübersicht)
2. [Funktionsumfang](#2-funktionsumfang)
3. [Technologie-Stack](#3-technologie-stack)
4. [Architektur](#4-architektur)
5. [Datenbankschema](#5-datenbankschema)
6. [Applikationssicherheit](#6-applikationssicherheit)
7. [Konfiguration & Deployment](#7-konfiguration--deployment)
8. [Projekt-Setup (Entwicklung)](#8-projekt-setup-entwicklung)

---

## 1. Projektübersicht

**Linkstack** ist eine Social-Media-inspirierte Link-Sharing-Plattform, auf der Benutzer eine persönliche Profilseite unter einer eigenen URL (`/@benutzername`) erstellen und ihre wichtigsten Links (Socials, Websites, Gaming-Profile etc.) zentral präsentieren können. Der Fokus liegt auf einfacher Bedienung, individuellem Design und einer sicheren, modernen Web-Architektur.

### Ziel des Moduls

Im Rahmen von Modul 183 («Applikationssicherheit implementieren») wurden gezielt Sicherheitsmechanismen gemäss den **OWASP Top 10 (2025)** eingebaut und dokumentiert. Das Ziel war nicht nur eine funktionierende Applikation, sondern eine, die branchenübliche Sicherheitsstandards erfüllt und potenzielle Angriffsvektoren aktiv mitigiert.

---

## 2. Funktionsumfang

### 2.1 Authentifizierung & Benutzerverwaltung

| Funktion | Beschreibung |
|---|---|
| Registrierung | Neues Konto anlegen mit Benutzername und Passwort |
| Login | Anmeldung mit Benutzername und Passwort |
| Logout | Sicheres Abmelden mit Session-Invalidierung und Cookie-Löschung |
| Validierung | Benutzername: 3–16 Zeichen, nur Buchstaben, Ziffern, Unterstrich; Passwort: min. 8, max. 256 Zeichen |

### 2.2 Profilseite (`/@benutzername`)

Jeder Benutzer erhält automatisch eine öffentlich erreichbare Profilseite:

- **Eigene URL** im Format `/@benutzername`
- **Aufrufs-Zähler** – zählt jeden Besucher nur einmal pro IP-Adresse
- **Individuelles Design** – Hintergrundfarbe, Textfarbe, Kartenfarbe und Icon-Farbe frei wählbar (Hex-Farbwerte)
- **Responsives Layout** – nutzbar auf Desktop und Mobilgeräten

### 2.3 Link-Verwaltung

Benutzer können über das Dashboard ihre Links verwalten:

- Links **hinzufügen** (Name, URL, Icon, Farbe)
- Links **entfernen**
- Auswahl aus **über 600 Vaadin-Icons** über einen visuellen Icon-Picker
- **URL-Validierung**: Nur `http://` und `https://` werden akzeptiert
- Zeichenlimits: Name max. 80 Zeichen, URL max. 2048 Zeichen

### 2.4 Community-Features

#### Kommentare

- Eingeloggte Benutzer können Kommentare auf Profilseiten hinterlassen (max. 500 Zeichen)
- Autoren können ihre eigenen Kommentare **bearbeiten** und **löschen**
- Seiteninhaber können **alle Kommentare** auf ihrer eigenen Seite löschen
- Kommentare können von Besuchern angezeigt oder vom Seiteninhaber deaktiviert werden (`show_comments`)

#### Likes

- Kommentare können **geliked** oder **disliked** werden
- Pro Benutzer und Kommentar ist nur eine Reaktion möglich (Toggle-Mechanismus)

### 2.5 Dashboard

Das Dashboard ist der zentrale Verwaltungsbereich für eingeloggte Benutzer:

| Tab | Inhalt |
|---|---|
| **Links** | Links hinzufügen, entfernen, Icon und Farbe wählen |
| **Kommentare** | Eigene Kommentare bearbeiten/löschen; alle Kommentare auf der eigenen Seite verwalten |
| **Style** | Hintergrund-, Text-, Karten- und Icon-Farbe der Profilseite anpassen |

---

## 3. Technologie-Stack

| Bereich | Technologie | Version | Zweck |
|---|---|---|---|
| Backend | Spring Boot (Kotlin) | 4.0.6 | Business-Logik, REST, Session-Handling |
| Frontend | Vaadin | 25.1.3 | Server-seitige UI-Komponenten |
| Datenbank | PostgreSQL | – | Persistente Datenspeicherung |
| Migration | Flyway | – | Versionierte Datenbankmigrationen |
| Sicherheit | Spring Security | 6.x | Authentifizierung, Autorisierung, CSRF |
| Build | Gradle (Kotlin DSL) | – | Build-Automatisierung |
| Container | Docker / Docker Compose | – | Lokale Entwicklungsumgebung |
| Java | JDK 21 | 21+ | Laufzeitumgebung |

---

## 4. Architektur

Die Applikation folgt einer klassischen **Schichtenarchitektur**:

```
┌─────────────────────────────────────────┐
│              Vaadin Views (UI)          │  ← Präsentationsschicht
│  LoginView, SignupView, Dashboard,      │
│  ProfilePageView, HomeView              │
├─────────────────────────────────────────┤
│           Service Layer                 │  ← Business-Logik
│  UserService, PageService,             │
│  ManagePageService, CommentService      │
├─────────────────────────────────────────┤
│          Repository Layer               │  ← Datenzugriff (Spring Data JPA)
│  UserRepository, PageRepository,       │
│  LinkRepository, CommentRepository,    │
│  LikeRepository, PageViewRepository    │
├─────────────────────────────────────────┤
│           PostgreSQL Datenbank          │  ← Persistenzschicht
│  (Flyway-verwaltet, Schema V1)         │
└─────────────────────────────────────────┘
```

### Wichtige Konfigurationsklassen

| Klasse | Aufgabe |
|---|---|
| `SecurityConfig` | Spring Security Konfiguration (CSRF, Headers, Logout, Rate-Limiting-Provider) |
| `RateLimitProperties` | Konfigurierbare Schwellenwerte für Rate-Limiting (via `application.yaml`) |
| `VaadinConfig` | Vaadin-Theme (Aura + Lumo Dark) und Viewport |

### Sicherheitsrelevante Klassen

| Klasse | Aufgabe |
|---|---|
| `LoginRateLimiter` | Zählt fehlgeschlagene Login-Versuche pro IP+Benutzername, sperrt bei Überschreitung |
| `RateLimitedAuthenticationProvider` | Wrapper um `DaoAuthenticationProvider`, prüft Rate-Limit vor Authentifizierung |
| `UserDetailsServiceImpl` | Spring Security Integration – lädt Benutzerdaten aus der Datenbank |

---

## 5. Datenbankschema

Das Schema wird durch Flyway automatisch beim Start erstellt und versioniert (`V1__init.sql`).

### Tabellen

#### `users`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | Eindeutige Benutzer-ID |
| `username` | VARCHAR | Eindeutiger Benutzername |
| `password_hash` | VARCHAR | BCrypt-Hash des Passworts |
| `page_id` | UUID (FK → pages) | Verknüpfte Profilseite |
| `created_at` / `updated_at` | TIMESTAMP | Zeitstempel (UTC) |

#### `pages`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | Seiten-ID |
| `user_id` | UUID (FK → users) | Besitzer der Seite |
| `background_color_hex` | VARCHAR | Hintergrundfarbe (#RRGGBB) |
| `text_color_hex` | VARCHAR | Textfarbe |
| `card_color_hex` | VARCHAR | Kartenfarbe |
| `icon_color_hex` | VARCHAR | Standard-Icon-Farbe |
| `show_comments` | BOOLEAN | Kommentare sichtbar? |

#### `links`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | Link-ID |
| `page_id` | UUID (FK → pages) | Zugehörige Seite |
| `name` | VARCHAR(255) | Anzeigename des Links |
| `href` | VARCHAR(255) | Ziel-URL |
| `icon` | VARCHAR | Vaadin-Icon-Name |
| `icon_color_hex` | VARCHAR | Icon-Farbe |

#### `comments`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | Kommentar-ID |
| `page_id` | UUID (FK → pages) | Seite, auf der der Kommentar steht |
| `author_id` | UUID (FK → users) | Autor des Kommentars |
| `content` | TEXT | Kommentartext |

#### `likes`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | Like-ID |
| `comment_id` | UUID (FK → comments) | Bezugnahme auf Kommentar |
| `user_id` | UUID (FK → users) | Benutzer, der den Like gegeben hat |

#### `page_views`
| Spalte | Typ | Beschreibung |
|---|---|---|
| `id` | UUID (PK) | View-ID |
| `page_id` | UUID (FK → pages) | Besuchte Seite |
| `ip_address` | VARCHAR(64) | IP-Adresse des Besuchers |
| `user_agent` | VARCHAR(512) | Browser/Client-Infos |
| `referer` | VARCHAR(512) | Herkunfts-URL |

### Wichtige Constraints

- `UNIQUE (ip_address, page_id)` in `page_views` → verhindert mehrfaches Zählen desselben Besuchers
- `UNIQUE (user_id)` in `pages` → ein Benutzer, eine Seite
- `UNIQUE (page_id)` in `users` → bidirektionale 1:1-Beziehung

---

## 6. Applikationssicherheit

Dieser Abschnitt ist der Kern dieser Dokumentation und zeigt, wie die im Modul geforderten Sicherheitsmassnahmen gemäss **OWASP Top 10 (2025)** umgesetzt wurden.

---

### 6.1 A07:2025 – Authentication Failures (Fehler in der Authentifizierung)

#### Risiko

Schwache Passwörter, Brute-Force-Angriffe auf den Login oder Session-Hijacking erlauben es Angreifern, fremde Konten zu übernehmen.

---

#### 6.1.1 BCrypt Passwort-Hashing

**Was:** Passwörter werden niemals im Klartext gespeichert, sondern mit **BCrypt** gehasht – einem adaptiven Hashing-Algorithmus, der absichtlich rechenintensiv ist.

**Warum sinnvoll:** Selbst bei einem vollständigen Datenbankdiebstahl sind die gespeicherten Hashes praktisch nicht umkehrbar. Der Salt wird automatisch eingebettet, sodass identische Passwörter unterschiedliche Hashes erzeugen.

```kotlin
// SecurityConfig.kt
@Bean
fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
```

---

#### 6.1.2 Rate-Limiting für Login-Versuche

**Was:** Nach **5 fehlgeschlagenen Login-Versuchen** (innerhalb von 300 Sekunden) wird der Login für die Kombination aus IP-Adresse und Benutzername gesperrt.

**Warum sinnvoll:** Verhindert automatisierte **Brute-Force-** und **Credential-Stuffing-Angriffe**, bei denen Angreifer systematisch Passwörter durchprobieren.

```kotlin
// LoginRateLimiter.kt
fun isBlocked(key: String): Boolean {
    val window = attemptsByKey[key] ?: return false
    if (window.isExpired()) { attemptsByKey.remove(key); return false }
    return window.count >= properties.loginMaxAttempts
}
```

Der Schlüssel wird aus IP **und** Benutzername gebildet (`{ip}|{username}`), sodass ein Angreifer weder durch Wechsel des Usernamens noch durch Wechsel der IP allein die Sperre trivial umgehen kann.

**Konfigurierbar** per Umgebungsprofil:

| Profil | Max. Versuche | Zeitfenster |
|---|---|---|
| `dev` | 10 | 60 Sekunden |
| `prod` | 5 | 300 Sekunden |

---

#### 6.1.3 Sichere Session-Cookies

**Was:** Der Session-Cookie (`JSESSIONID`) wird mit mehreren Schutz-Flags gesetzt:

```yaml
# application.yaml / application-prod.yaml
server:
  servlet:
    session:
      cookie:
        http-only: true   # JavaScript kann den Cookie nicht lesen
        secure: true      # Cookie nur über HTTPS übertragen (prod)
        same-site: strict # Cookie wird nicht bei Cross-Site-Requests mitgesendet (prod)
```

**Warum sinnvoll:**
- `HttpOnly` verhindert, dass schädliches JavaScript (XSS-Angriff) den Session-Cookie ausliest und für Session-Hijacking missbraucht
- `Secure` stellt sicher, dass der Cookie nur über HTTPS übertragen wird, sodass er nicht im Klartext abgefangen werden kann
- `SameSite=Strict` schützt vor CSRF-Angriffen auf Cookie-Ebene (ergänzt den serverseitigen CSRF-Schutz)

---

#### 6.1.4 Session-Invalidierung beim Logout

**Was:** Beim Abmelden wird die HTTP-Session serverseitig vollständig invalidiert und der Cookie gelöscht.

```kotlin
// SecurityConfig.kt
.logout { logout ->
    logout
        .logoutSuccessUrl("${Routes.LOGIN}?logout")
        .invalidateHttpSession(true)
        .deleteCookies(JSESSIONID)
}
```

**Warum sinnvoll:** Verhindert, dass ein nach dem Logout gestohlener Cookie noch gültig ist (**Session-Fixation**-Schutz).

---

### 6.2 A01:2025 – Broken Access Control (Fehler in der Zugriffskontrolle)

#### Risiko

Benutzer können auf Ressourcen oder Aktionen zugreifen, für die sie keine Berechtigung haben (z. B. Kommentare anderer Benutzer löschen).

---

#### 6.2.1 Object-Level Security in der Service-Schicht

**Was:** Jede schreibende Operation prüft, ob der aktuell eingeloggte Benutzer die Berechtigung für die jeweilige Ressource besitzt.

Beispiele:
- **Links entfernen:** Nur wenn der Link zur eigenen Seite gehört
- **Kommentar bearbeiten:** Nur der Autor selbst darf editieren
- **Kommentar löschen:** Autor **oder** Seiteninhaber darf löschen
- **Seitenstil ändern:** Nur der Seitenbesitzer

```kotlin
// ManagePageService.kt – Beispiel Link-Löschung
fun removeLink(linkId: UUID) {
    val link = linkRepository.findById(linkId)
    val currentPage = getCurrentUserPage()
    check(link.page.id == currentPage.id) { "Not your link" }
    linkRepository.delete(link)
}
```

**Warum sinnvoll:** Verhindert **Insecure Direct Object Reference (IDOR)**-Angriffe, bei denen ein Angreifer eine fremde Ressourcen-ID in einer Anfrage einschmuggelt.

---

#### 6.2.2 CSRF-Schutz

**Was:** Alle zustandsverändernden Anfragen werden durch ein **CSRF-Token** geschützt, das in einem Cookie gespeichert und bei jeder POST-Anfrage validiert wird.

```kotlin
// SecurityConfig.kt
.csrf { csrf ->
    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
}
```

**Warum sinnvoll:** Verhindert **Cross-Site Request Forgery (CSRF)**, bei dem eine bösartige Website im Hintergrund Anfragen im Namen des eingeloggten Benutzers stellt (z. B. Links löschen, Passwort ändern).

---

### 6.3 A03:2025 – Injection (SQL-Injection, XSS)

#### Risiko

Benutzereingaben (Kommentare, Linknamen, URLs) werden unkontrolliert in Datenbankabfragen oder HTML eingebettet und ermöglichen SQL-Injection oder Cross-Site-Scripting (XSS).

---

#### 6.3.1 SQL-Injection-Schutz via Spring Data JPA

**Was:** Die gesamte Datenbankinteraktion erfolgt über **Spring Data JPA** (Hibernate). Alle Datenbankoperationen verwenden **Prepared Statements** mit parametrisierten Abfragen.

**Warum sinnvoll:** SQL-Injection ist unmöglich, weil Benutzereingaben nie direkt in SQL-Strings konkateniert werden. Die Datenbank-Treiberschicht behandelt Parameterwerte grundsätzlich als Daten, nicht als SQL-Code.

---

#### 6.3.2 XSS-Schutz durch Vaadin und CSP

**Was:** Vaadin rendert alle UI-Komponenten server-seitig und escaped Eingaben standardmässig. Zusätzlich schützt ein **Content Security Policy (CSP)**-Header den Browser:

```kotlin
// SecurityConfig.kt
private const val CSP_POLICY =
    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
    "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
    "font-src 'self' data:; connect-src 'self' ws: wss:; frame-ancestors 'none'"
```

**Warum sinnvoll:** Die CSP schränkt ein, woher der Browser Ressourcen laden und Scripts ausführen darf. `frame-ancestors 'none'` verhindert zudem, dass die Seite in einem `<iframe>` eingebettet wird (**Clickjacking**-Schutz, ergänzt `X-Frame-Options`).

*Hinweis:* `'unsafe-inline'` und `'unsafe-eval'` sind für das Vaadin-Framework erforderlich und stellen eine bekannte Einschränkung der CSP-Stärke dar. Vaadin benötigt diese Direktiven für seine interne Rendering-Engine.

---

#### 6.3.3 Eingabevalidierung

**Was:** Alle Benutzereingaben werden auf mehreren Ebenen validiert:

1. **DTO-Ebene** (Bean Validation):
```kotlin
// SignUpDto.kt
@field:Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters.")
@field:Pattern(regexp = "^[A-Za-z0-9_]+\$", message = "Username may only contain letters, digits and underscore.")
val username: String

@field:Size(min = 8, max = 256, message = "Password must be at least 8 characters long.")
val password: String
```

2. **Vaadin-Ebene** (UI-seitige Zeichenlimits auf Textfelder):
   - Kommentare: max. 500 Zeichen
   - Linknamen: max. 80 Zeichen
   - URLs: max. 2048 Zeichen

3. **Service-Ebene**:
   - URL-Scheme-Whitelist: Nur `http://` und `https://` erlaubt
   - Farb-Validierung: Regex `^#[0-9A-Fa-f]{6}$`

**Warum sinnvoll:** Mehrschichtige Validierung stellt sicher, dass ungültige oder bösartige Eingaben weder die Datenbank kompromittieren noch in der UI als schädlicher Code ausgeführt werden können.

---

### 6.4 A05:2025 – Security Misconfiguration (Fehlkonfiguration)

#### Risiko

Standard-Konfigurationen von Spring Boot / Docker können Debug-Informationen, offene Endpoints oder unsichere Standardwerte exponieren.

---

#### 6.4.1 Sichere HTTP-Security-Headers

**Was:** Neben CSP werden weitere Schutzheader gesetzt:

| Header | Wert | Schutz gegen |
|---|---|---|
| `X-Frame-Options` | `DENY` | Clickjacking (Einbettung in `<iframe>`) |
| `Strict-Transport-Security` | max-age=31536000; includeSubDomains | Erzwingt HTTPS für 1 Jahr |
| `Content-Security-Policy` | siehe oben | XSS, Clickjacking, unerwünschte Ressourcen |

```kotlin
// SecurityConfig.kt
.headers { headers ->
    headers
        .frameOptions { it.deny() }
        .contentSecurityPolicy { csp -> csp.policyDirectives(CSP_POLICY) }
        .httpStrictTransportSecurity { hsts ->
            hsts.includeSubDomains(true).maxAgeInSeconds(31536000L)
        }
}
```

---

#### 6.4.2 Produktionskonfiguration

**Was:** Über ein separates Spring-Profil (`prod`) werden in der Produktion:

- **Stack-Traces deaktiviert** – Fehlermeldungen enthalten keine internen Details
- **SQL-Logging deaktiviert** – Keine Datenbank-Queries in Log-Dateien
- **Log-Level erhöht** – Root-Level `WARN`, nur Applikationslog auf `INFO`
- **Docker Compose deaktiviert** – Kein automatischer Container-Start in Produktion

```yaml
# application-prod.yaml
spring:
  jpa:
    show-sql: false
  web:
    error:
      include-stacktrace: never
      include-message: never
logging:
  level:
    root: WARN
    lol.linkstack: INFO
```

**Warum sinnvoll:** Stack-Traces und SQL-Queries in Fehlermeldungen liefern Angreifern wertvolle Informationen über die interne Applikationsstruktur (**Information Disclosure**).

---

#### 6.4.3 Minimale Actuator-Exposition

**Was:** Spring Boot Actuator ist aktiviert, aber es wird nur der `/actuator/health`-Endpoint exponiert, und dieser ohne Details.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: never
```

**Warum sinnvoll:** Actuator-Endpoints können sensible Informationen über die laufende Applikation preisgeben (Environment-Variablen, Heap-Dump, etc.). Die minimale Exposition reduziert die Angriffsfläche erheblich.

---

#### 6.4.4 Datenbankzugangsdaten via Umgebungsvariablen

**Was:** In der Produktionskonfiguration werden keine Zugangsdaten hartkodiert. Sie werden ausschliesslich über Umgebungsvariablen injiziert:

```yaml
# application-prod.yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME}
```

**Warum sinnvoll:** Verhindert, dass Zugangsdaten im Quellcode oder in der Versionsverwaltung landen (**Secrets in Source Code**).

---

### 6.5 Zusammenfassung der Sicherheitsmassnahmen

| OWASP Kategorie | Massnahme | Implementiert |
|---|---|---|
| **A07 – Auth Failures** | BCrypt Passwort-Hashing | ✅ |
| **A07 – Auth Failures** | Rate-Limiting Login (5 Versuche / 5 Min) | ✅ |
| **A07 – Auth Failures** | HttpOnly + Secure + SameSite Cookies | ✅ |
| **A07 – Auth Failures** | Session-Invalidierung beim Logout | ✅ |
| **A01 – Broken Access Control** | Object-Level Security in Services | ✅ |
| **A01 – Broken Access Control** | CSRF-Schutz (Cookie-Token) | ✅ |
| **A03 – Injection** | Prepared Statements via JPA | ✅ |
| **A03 – Injection** | Content Security Policy (CSP) | ✅ |
| **A03 – Injection** | Mehrstufige Eingabevalidierung | ✅ |
| **A05 – Misconfiguration** | X-Frame-Options DENY | ✅ |
| **A05 – Misconfiguration** | HSTS (1 Jahr, inkl. Subdomains) | ✅ |
| **A05 – Misconfiguration** | Keine Stack-Traces in Produktion | ✅ |
| **A05 – Misconfiguration** | Minimale Actuator-Exposition | ✅ |
| **A05 – Misconfiguration** | Secrets via Umgebungsvariablen | ✅ |

---

## 7. Konfiguration & Deployment

### 7.1 Spring-Profile

| Profil | Aktiviert durch | Zweck |
|---|---|---|
| `dev` (Standard) | `SPRING_PROFILES_ACTIVE=dev` | Lokale Entwicklung mit Docker Compose, relaxiertem Rate-Limiting, SQL-Logging |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` | Produktionsbetrieb mit verschärften Sicherheitseinstellungen |

### 7.2 Umgebungsvariablen (Produktion)

| Variable | Beschreibung |
|---|---|
| `DB_USERNAME` | Datenbank-Benutzername |
| `DB_PASSWORD` | Datenbank-Passwort |
| `DB_HOST` | Datenbank-Hostname |
| `DB_PORT` | Datenbank-Port (Standard: 5432) |
| `DB_NAME` | Datenbankname |
| `SPRING_PROFILES_ACTIVE` | Aktives Spring-Profil (`prod`) |

### 7.3 Docker Compose (Entwicklung)

Für die lokale Entwicklung stellt `compose.yaml` eine PostgreSQL-Instanz bereit, die Spring Boot automatisch startet.

---

## 8. Projekt-Setup (Entwicklung)

### Voraussetzungen

- Docker CLI
- IntelliJ IDEA (oder andere JVM-fähige IDE)
- JDK 21+

### Installation

1. Repository klonen:
```bash
git clone https://github.com/nijasc/Modul183-Linus-Louis-Nils.git
cd Modul183-Linus-Louis-Nils
```

2. Umgebungsvariablen einrichten:
```bash
cp example.env local.env
```

3. Projekt in IntelliJ IDEA öffnen

4. Projekt mit der vordefinierten **Run**-Konfiguration starten

Die Applikation startet standardmässig im `dev`-Profil. Docker Compose wird automatisch ausgeführt und stellt die PostgreSQL-Datenbank bereit. Flyway führt das Datenbankschema (`V1__init.sql`) beim ersten Start automatisch aus.

Die Applikation ist anschliessend unter `http://localhost:8080` erreichbar.
