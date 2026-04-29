# Linkstack

## Über das Projekt

**Name:** Linkstack

**Idee:** Eine Social Media Link-Sharing Plattform mit Fokus auf Gaming-Integration (z. B. Discord RPC, Spotify).

**Dokumentation:** [Dokumentation](/dokumentation/Dokumentation.md)

---

## Geplante Features

### Authentication

- Login
- Registrierung
- Logout

### Edit Page

- Links hinzufügen und entfernen
- Grundlegende Styling-Optionen
- Optional:
    - Discord RPC Integration
    - Spotify Integration

### Community Features

- Kommentare auf Profilen
    - Kommentare hinzufügen
    - Eigene Kommentare löschen
    - Kommentare liken

---

## Tech Stack

| Bereich    | Technologie     | Zweck                                 |
|------------|-----------------|---------------------------------------|
| Backend    | Spring Boot     | Business-Logik                        |
| Frontend   | Vaadin          | Benutzeroberfläche                    |
| Datenbank  | PostgreSQL      | Persistente Datenspeicherung          |
| Migration  | Flyway          | Datenbank-Versionierung               |
| Sicherheit | Spring Security | Authentifizierung & Zugriffskontrolle |

---

## Sicherheitsanforderungen

| Risiko (OWASP 2025)                              | Warum?                                                                                                                   | Implementierungsbeispiele                                                                                                                                                                                                           |
|--------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **A01:2025 – Broken Access Control** *(Pflicht)* | **Zentral für die App!** Benutzer können nur ihre eigenen Links/Kommentare bearbeiten.                                   | - **Role-Based Access Control (RBAC)**. <br> - **Object-Level Security** (z. B. nur der Besitzer darf Kommentare löschen). <br> - **CSRF-Schutz** für sensitive Aktionen.                                                           |
| **A05:2025 – Injection**                         | **Klassisches Risiko** für Web-Apps mit Benutzereingaben (Kommentare, Links, Suchfelder).                                | - **Vaadin-Sanitization** (z. B. `TextField` mit `setMaxLength`). <br> - **Prepared Statements** (Spring Data JPA schützt vor SQLi). <br> - **Content Security Policy (CSP)** gegen XSS.                                            |
| **A07:2025 – Authentication Failures**           | **Login/Registrierung ist kritisch!** Schwache Passwörter, Session-Hijacking oder Brute-Force-Angriffe sind möglich.     | - **Password-Encoding** (BCrypt mit Spring Security). <br> - **Secure Cookies** (HttpOnly, Secure, SameSite). <br> - **Rate-Limiting** für Login-Versuche. <br> - **2FA** (optional, z. B. mit TOTP).                               |
| **A02:2025 – Security Misconfiguration**         | **Standardrisiko** für Spring Boot + Docker. Fehlende HTTPS, offene Debug-Endpoints oder Default-Passwörter sind häufig. | - **Secure Headers** (HSTS, CSP, X-Frame-Options). <br> - **Deaktivierung von Debug-Modi** in Produktion. <br> - **Minimale Docker-Images** (keine Root-Rechte). <br> - **Flyway-Sicherheit** (keine SQL-Injection in Migrationen). |

---

## Projekt Setup

### Voraussetzungen

- Docker CLI
- IntelliJ IDEA (oder andere IDE)
- JDK 21+

### Installation

1. Repository klonen:
   ```bash
   git clone https://github.com/nijasc/Modul183-Linus-Louis-Nils.git
   ```

2. Umgebungsvariablen einrichten:
   ```bash
   cd Modul183-Linus-Louis-Nils
   cp example.env local.env
   ```

3. Projekt in IntelliJ IDEA öffnen

4. Projekt mit der vordefinierten "Run"-Konfiguration starten
