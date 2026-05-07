# Linkstack

## Über das Projekt

**Name:** Linkstack

**Idee:** Eine Social Media Link-Sharing Plattform mit Fokus auf Gaming-Integration (z. B. Discord RPC, Spotify).

**Dokumentation:** [Dokumentation](/dokumentation/Dokumentation.md)

---

## Features

### Authentication

- Login
- Registrierung
- Logout
- Secure password encoding (BCrypt)
- CSRF protection
- Secure cookies

### Profile Page

- Custom URL (@username)
- View counter (unique per IP)
- Custom styling options (background color, text color, card color, icon color)
- Responsive design

### Link Management

- Links hinzufügen und entfernen
- Custom icons from icon grid picker (over 600+ Vaadin icons)
- Custom icon colors
- Drag-and-drop support (coming soon)

### Community Features

- Kommentare auf Profilen
    - Kommentare hinzufügen (when logged in)
    - Eigene Kommentare löschen
    - Eigene Kommentare bearbeiten
    - Kommentare liken/disliken
    - Manage comments on dashboard (delete any comment on your page)
- Profile views tracking (unique per IP address)

### Dashboard

- Modern, tabbed interface
- Link management with icon grid picker
- Comment management (view, edit, delete)
- Page style customization (colors)

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

---

## New Features & Improvements

### Latest Updates:

- ✅ NAVBAR: Login/Signup only visible to non-logged in users
- ✅ NAVBAR: Dashboard button only visible to logged in users
- ✅ PROFILE: Views only increment once per IP address
- ✅ HOMEPAGE: Modern homepage with hero section, features, how it works, and CTA
- ✅ STYLING: All pages restyled with modern, clean design
- ✅ COMMENTS: Full comment system with add, edit, delete
- ✅ COMMENTS: Like system for comments
- ✅ COMMENTS: Site owner can delete any comment on their page
- ✅ COMMENTS: Users can edit and delete their own comments
- ✅ DASHBOARD: Comment management section
- ✅ DASHBOARD: Modern tabbed interface
- ✅ ICONS: Replaced combobox with visual icon grid picker
- ✅ ICONS: Added icon colors for links
- ✅ STYLING: Page style customization (background, text, card, icon colors)
- ✅ UI/UX: Smooth animations and transitions
- ✅ UI/UX: More icons throughout the app
- ✅ UI/UX: Modern cards with hover effects
- ✅ SECURITY: CSRF protection enabled
- ✅ SECURITY: Content Security Policy headers
- ✅ SECURITY: Secure headers (X-Frame-Options, etc.)
- ✅ ARCHITECTURE: Modular component-based architecture
- ✅ CODE QUALITY: Clean code, no magic numbers
- ✅ PROFESSIONAL: App feels professional yet enjoyable to use
