-- =====================================================
-- Projet Differencia : création des tables + données de test
-- SGBD : MySQL 8
-- Usage : mysql -u root -p < sql/differencia.sql
--
-- ATTENTION : ce script SUPPRIME puis recrée les 3 tables.
-- (Facultatif : l'application crée aussi les tables avec Hibernate
--  et charge les mêmes données avec DataLoader.)
-- =====================================================

CREATE DATABASE IF NOT EXISTS differencia
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE differencia;

DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS affectation;
DROP TABLE IF EXISTS activite;
DROP TABLE IF EXISTS apprenant;

-- ---------- Table 1 : Apprenant ----------
CREATE TABLE apprenant (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(255) NOT NULL,
    prenom        VARCHAR(255) NOT NULL,
    groupe        VARCHAR(255) NOT NULL,
    niveau_actuel VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_apprenant_niveau
        CHECK (niveau_actuel IN ('DEBUTANT', 'INTERMEDIAIRE', 'AVANCE'))
);

-- ---------- Table : Utilisateur (authentification) ----------
-- Aucun compte n'est inséré ici : au premier démarrage, l'application crée
-- le compte admin (voir application.properties) avec un mot de passe haché BCrypt.
CREATE TABLE utilisateur (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    nom_complet   VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    actif         BIT          NOT NULL DEFAULT 1,
    date_creation DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_utilisateur_username UNIQUE (username),
    CONSTRAINT chk_utilisateur_role CHECK (role IN ('ADMIN', 'ENSEIGNANT'))
);

-- ---------- Table 2 : Activite ----------
CREATE TABLE activite (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    titre                VARCHAR(255) NOT NULL,
    discipline           VARCHAR(255) NOT NULL,
    objectif_pedagogique TEXT,
    consignes            TEXT,
    niveau_difficulte    VARCHAR(20)  NOT NULL,
    duree_estimee        INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_activite_niveau
        CHECK (niveau_difficulte IN ('DEBUTANT', 'INTERMEDIAIRE', 'AVANCE')),
    CONSTRAINT chk_activite_duree CHECK (duree_estimee >= 1)
);

-- ---------- Table 3 : Affectation (association) ----------
CREATE TABLE affectation (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    apprenant_id     BIGINT      NOT NULL,
    activite_id      BIGINT      NOT NULL,
    date_affectation DATE,
    date_limite      DATE,
    statut           VARCHAR(20) NOT NULL DEFAULT 'A_FAIRE',
    resultat         INT,
    commentaire      TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_affectation_apprenant FOREIGN KEY (apprenant_id) REFERENCES apprenant (id),
    CONSTRAINT fk_affectation_activite  FOREIGN KEY (activite_id)  REFERENCES activite (id),
    CONSTRAINT chk_affectation_statut
        CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'TERMINE')),
    CONSTRAINT chk_affectation_resultat CHECK (resultat IS NULL OR resultat BETWEEN 0 AND 100)
);

-- ---------- Données de test (identiques à DataLoader) ----------
INSERT INTO apprenant (nom, prenom, groupe, niveau_actuel) VALUES
 ('Chafiqi',   'Marouane', 'G1', 'DEBUTANT'),
 ('Aarkab',    'Walid',    'G1', 'DEBUTANT'),
 ('Tazi',      'Imane',    'G1', 'DEBUTANT'),
 ('Khalloufi', 'Simo',     'G2', 'INTERMEDIAIRE'),
 ('Louadi',    'Nada',     'G2', 'INTERMEDIAIRE'),
 ('Amali',     'Said',     'G2', 'INTERMEDIAIRE'),
 ('Benkirane', 'Hamza',    'G2', 'INTERMEDIAIRE'),
 ('Najem',     'Malak',    'G3', 'AVANCE'),
 ('Wafi',      'Samira',   'G3', 'AVANCE'),
 ('Idrissi',   'Anas',     'G3', 'AVANCE');

INSERT INTO activite (titre, discipline, objectif_pedagogique, consignes, niveau_difficulte, duree_estimee) VALUES
 ('Additions simples',          'Mathématiques', 'Maîtriser l''addition',           'Résoudre 10 additions.',            'DEBUTANT',      20),
 ('Lecture guidée',             'Français',      'Lire un texte court',             'Lire et répondre à 5 questions.',   'DEBUTANT',      25),
 ('Équations du 1er degré',     'Mathématiques', 'Résoudre une équation',           'Résoudre 8 équations.',             'INTERMEDIAIRE', 40),
 ('Rédaction d''un paragraphe', 'Français',      'Structurer un texte',             'Écrire un paragraphe de 8 lignes.', 'INTERMEDIAIRE', 35),
 ('Problème ouvert',            'Mathématiques', 'Raisonner et justifier',          'Résoudre un problème complexe.',    'AVANCE',        60),
 ('Analyse de texte',           'Français',      'Analyser un texte littéraire',    'Rédiger une analyse de 1 page.',    'AVANCE',        50),
 ('Mini-projet sciences',       'Sciences',      'Mener une démarche scientifique', 'Réaliser une expérience.',          'INTERMEDIAIRE', 90),
 ('Vocabulaire de base',        'Anglais',       'Apprendre 20 mots',               'Associer mots et images.',          'DEBUTANT',      15);

INSERT INTO affectation (apprenant_id, activite_id, date_affectation, date_limite, statut, resultat, commentaire) VALUES
 (1, 1,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  85,   'Très bien'),
 (1, 8,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'EN_COURS', NULL, NULL),
 (2, 2,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  60,   'Doit s''entraîner'),
 (3, 2,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  70,   'Bon effort'),
 (4, 3,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  75,   'Bon travail'),
 (5, 4,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'A_FAIRE',  NULL, NULL),
 (6, 7,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'EN_COURS', NULL, NULL),
 (7, 4,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  68,   'Peut mieux structurer'),
 (8, 5,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  92,   'Excellent'),
 (9, 3,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'EN_COURS', NULL, 'Activité d''un niveau inférieur (révision)'),
 (10, 6, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'TERMINE',  88,   'Très bonne analyse');

-- Vérification : moyenne des résultats par niveau d'apprenant
SELECT ap.niveau_actuel, COUNT(*) AS nb_affectations, ROUND(AVG(af.resultat), 1) AS moyenne
FROM affectation af JOIN apprenant ap ON ap.id = af.apprenant_id
GROUP BY ap.niveau_actuel;
