<div align="center">

# 🎓 Differencia

**Application web de gestion de la pédagogie différenciée : l'enseignant suit chaque apprenant selon son niveau.**

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-6DB33F?logo=springsecurity&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3-005F0F?logo=thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?logo=bootstrap&logoColor=white)

</div>

---

## 📌 Présentation

En classe, tous les apprenants n'ont pas le même rythme. **Differencia** permet à l'enseignant de :

- classer ses apprenants par **niveau** (Débutant, Intermédiaire, Avancé) ;
- constituer un **catalogue d'activités** avec une difficulté et une durée estimée ;
- **affecter** à chaque apprenant l'activité adaptée à son niveau ;
- **suivre l'avancement** et les résultats sur un tableau de bord.

> 🚧 **État du projet** : cette version couvre l'**espace enseignant**. L'espace apprenant (connexion de l'étudiant, consultation de ses activités) est prévu, voir la [feuille de route](#-feuille-de-route).

---

## 📸 Aperçu

| Connexion | Tableau de bord |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/6920aa7d-0cb8-43b7-9912-490e127b159d" alt="Connexion" width="400"/> | <img src="https://github.com/user-attachments/assets/0edd7b62-bfba-4fa2-99b3-7fce0244b415" alt="Tableau de bord" width="400"/> |

| Apprenants | Catalogue d'activités |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/7da8f4c5-b926-4c60-b3f4-e38593cc3a51" alt="Apprenants" width="400"/> | <img src="https://github.com/user-attachments/assets/2eb207ee-37c8-42b2-821b-a70188392b6b" alt="Activités" width="400"/> |

| Suivi des réalisations | Affecter une activité |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/cbfa21f5-3e0d-4dc2-bf9a-aaa99ad1a576" alt="Suivi" width="400"/> | <img src="https://github.com/user-attachments/assets/16293a46-80e8-4cef-bd07-5a2a9e982abe" alt="Affecter" width="400"/> |
---

## ✨ Fonctionnalités de l'espace enseignant

### 🔐 Authentification
- Connexion par formulaire (Spring Security), option « Se souvenir de moi ».
- Menu utilisateur : changement de mot de passe et déconnexion.
- Accès à toutes les pages réservé aux utilisateurs connectés.

### 📊 Tableau de bord
- Chiffres clés : nombre d'apprenants, d'activités, d'affectations, moyenne générale.
- Résultats par niveau (nombre d'affectations, terminées, moyenne).
- État d'avancement global : à faire / en cours / terminées.
- Liste des **activités en retard** et des dernières affectations.

### 👩‍🎓 Apprenants
- Liste avec **recherche** (nom, prénom, groupe) et **filtre par niveau**.
- Ajout, modification, fiche détaillée de l'apprenant (ses affectations, sa moyenne).
- Affectation directe d'une activité depuis la liste.

### 📚 Activités
- Catalogue sous forme de cartes : titre, discipline, objectif pédagogique, consignes, durée.
- Recherche par titre / discipline et filtre par difficulté.
- Création et modification d'activités.

### ✅ Affectation et suivi
- Choix d'un apprenant : les activités de **son niveau sont suggérées en premier**.
- Suivi de chaque réalisation : statut (*À faire*, *En cours*, *Terminé*), **résultat sur 100** et commentaire.
- Détection automatique des **retards** par rapport à la date limite.
- Filtre par statut et recherche par apprenant ou activité.

> ℹ️ Les **suppressions** (apprenants, activités, affectations) sont réservées au rôle `ADMIN`. L'enseignant ne les voit pas.

---

## 🛠️ Technologies

| Couche | Technologie |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot 3.3.5 (Web, Data JPA, Validation) |
| Sécurité | Spring Security 6, mots de passe BCrypt |
| Vues | Thymeleaf + `thymeleaf-extras-springsecurity6` |
| Base de données | MySQL 8 (Hibernate, `ddl-auto=update`) |
| Interface | Bootstrap 5.3, Bootstrap Icons |
| Outils | Maven, Lombok |
| Tests | JUnit 5, Mockito, Spring Security Test, H2 (tests uniquement) |

---

## 🗂️ Structure du projet

```
src/main/java/projet/differencia/ma
├── config/        DataLoader (données de démo), UtilisateurInitializer (comptes)
├── controller/    Dashboard, Apprenant, Activite, Affectation, Auth, Profil, Utilisateur
├── dto/           Formulaires (UtilisateurForm, ChangerMotDePasseForm)
├── entities/      Apprenant, Activite, Affectation, Utilisateur, Niveau, Statut, Role
├── repository/    Interfaces Spring Data JPA
├── security/      SecurityConfig, UtilisateurDetailsService, verrouillage de compte
└── service/       Logique métier
src/main/resources
├── templates/     Pages Thymeleaf (apprenants, activites, affectations, utilisateurs...)
└── static/css/    style.css
sql/differencia.sql   Script SQL optionnel (tables + données de test)
```

### Modèle de données

```
Apprenant (1) ───< Affectation >─── (1) Activite
 nom, prenom,       dateAffectation,     titre, discipline,
 groupe,            dateLimite, statut,  objectifPedagogique, consignes,
 niveauActuel       resultat, commentaire niveauDifficulte, dureeEstimee

Utilisateur : username, password (BCrypt), nomComplet, role, actif
```

---

## 🚀 Installation

### Prérequis
- JDK 17+
- MySQL 8 démarré en local (port 3306)
- Maven (ou celui intégré à IntelliJ IDEA)

### Lancer le projet

```bash
git clone https://github.com/<votre-compte>/<votre-repo>.git
cd <votre-repo>
```

1. Vérifier `src/main/resources/application.properties` (utilisateur et mot de passe MySQL).
   La base `differencia` est **créée automatiquement**.
2. Lancer :
   ```bash
   mvn spring-boot:run
   ```
   *(ou, dans IntelliJ : ouvrir le `pom.xml` en tant que projet, puis exécuter `DifferenciaApplication`)*
3. Ouvrir <http://localhost:8080>.

Au premier démarrage, l'application crée les tables, charge des **données de démonstration** et un **compte enseignant** :

| Rôle | Utilisateur | Mot de passe |
|---|---|---|
| Enseignant | `prof` | `Prof@12345` |

> ⚠️ Comptes de démonstration uniquement. En production : changer les mots de passe, mettre `app.demo-users=false` et définir la variable d'environnement `REMEMBER_ME_KEY`.

---

## 🔒 Sécurité

- Mots de passe hachés avec **BCrypt** (8 à 72 caractères, lettre + chiffre).
- Protection **CSRF** sur tous les formulaires, déconnexion en `POST`.
- Session protégée contre la fixation, cookie `HttpOnly` + `SameSite=Lax`.
- **Verrouillage du compte 15 minutes** après 5 échecs de connexion.
- Message d'erreur de connexion volontairement générique.
- Deux rôles : `ENSEIGNANT` (espace de travail) et `ADMIN` (suppressions et gestion des utilisateurs).

---

## 🧪 Tests

```bash
mvn test
```

- Tests unitaires des services (règles métier).
- Tests d'intégration de la sécurité (redirection vers la connexion, rôles, CSRF, verrouillage) avec une base H2 en mémoire : **MySQL n'est pas nécessaire** pour les tests.

---

## 🗺️ Feuille de route

- [x] Espace **enseignant** : apprenants, activités, affectations, suivi, tableau de bord
- [x] Authentification et rôles
- [ ] Espace **apprenant** : connexion de l'étudiant (nouveau rôle `APPRENANT`, lien `Utilisateur` ↔ `Apprenant`)
- [ ] L'apprenant consulte les activités qui lui sont affectées et met à jour son avancement
- [ ] Remise de travaux et retour de l'enseignant
- [ ] Export des résultats (PDF / Excel)

---

## 👤 Auteur

**Marouane Chafiqi**

---

<div align="center">Projet réalisé dans un cadre informatique & pédagogique 🎓</div>
