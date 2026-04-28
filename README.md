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

Priorisierte OWASP Top 10 (2025) Risiken:
- A01:2025 – Broken Access Control
- A02:2025 – Security Misconfiguration
- A03:2025 – Software Supply Chain Failures
- A06:2025 – Insecure Design

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
