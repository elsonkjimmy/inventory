# 📘 Document d’Analyse des Besoins

## 🗂️ Système de Gestion de Stock

---

## 1️⃣ Contexte du projet

Le projet consiste à développer un **Système de Gestion de Stock** pour une entreprise.

### 🎯 Objectifs globaux

* Automatiser les ventes et la gestion des produits
* Réduire les erreurs, fraudes et pertes
* Fournir des rapports et statistiques fiables
* Assurer la sécurité des données et des accès
* Offrir une interface simple et réactive

📌 Application **locale** avec envoi de **mails et notifications**
👥 Équipe : **2 personnes**
⏱️ Durée : **3 jours**

---

## 2️⃣ Objectifs principaux

* Authentification par rôle (**Admin / Gestionnaire**)
* Gestion des produits et catégories (**CRUD**)
* Suivi des ventes et génération de reçus
* Alertes automatiques de stock faible
* Rapports détaillés (ventes, stock, CA)
* Sécurité, performance et simplicité

---

## 3️⃣ Acteurs

| Acteur             | Description          | Actions principales                                                                     |
| ------------------ | -------------------- | --------------------------------------------------------------------------------------- |
| **Administrateur** | Gère tout le système | CRUD utilisateurs et produits, promotions, tableaux de bord, rapports, ventes si besoin |
| **Gestionnaire**   | Utilisateur métier   | Ventes, gestion produits, tableau de bord personnel, reçus, suivi stock                 |
| **Client**         | Acheteur             | Paiement, réception du reçu                                                             |

---

## 4️⃣ Besoins Fonctionnels

| ID   | Besoin Fonctionnel    | Description                           |
| ---- | --------------------- | ------------------------------------- |
| BF1  | Connexion utilisateur | Login / mot de passe selon rôle       |
| BF2  | Gestion utilisateurs  | Création, modification, suppression   |
| BF3  | Ajouter produit       | Nom, catégorie, prix, quantité, seuil |
| BF4  | Modifier produit      | Mise à jour infos produit             |
| BF5  | Supprimer produit     | Suppression avec confirmation         |
| BF6  | Rechercher produit    | Par nom ou catégorie                  |
| BF7  | Filtrer produits      | Catégorie, prix, stock, fournisseur   |
| BF8  | Enregistrer vente     | Produits, quantités, prix             |
| BF9  | Mise à jour stock     | Déduction automatique                 |
| BF10 | Générer reçu          | PDF / imprimable                      |
| BF11 | Promotions            | Réductions appliquées                 |
| BF12 | Historique ventes     | Par période                           |
| BF13 | Alertes stock faible  | Notification seuil                    |
| BF14 | Rapports              | Ventes, produits, CA                  |

---

## 5️⃣ Besoins Non Fonctionnels

| ID   | Description        |
| ---- | ------------------ |
| BNF1 | Sécurité           |
| BNF2 | Performance        |
| BNF3 | Disponibilité      |
| BNF4 | Simplicité         |
| BNF5 | Notifications      |
| BNF6 | Reporting          |
| BNF7 | Multi-utilisateurs |

---

## 6️⃣ Règles Métiers

* Seul l’**admin** gère les utilisateurs et promotions
* Chaque employé a un tableau de bord personnel
* Toute vente génère une facture
* Stock sous seuil → notification
* Produits périmés interdits à la vente
* Produits proches de péremption → promotion possible
* Ajout de produit validé par l’admin

---

## 7️⃣ User Stories (Agile)

| ID   | User Story            | Priorité | Points | Critères d’acceptation                 |
| ---- | --------------------- | -------- | ------ | -------------------------------------- |
| US1  | Connexion utilisateur | M        | 3      | Accès autorisé si identifiants valides |
| US2  | Gestion des rôles     | M        | 2      | Droits corrects selon rôle             |
| US3  | CRUD utilisateurs     | M        | 5      | Confirmation requise                   |
| US4  | Déconnexion auto      | S        | 2      | 30 min d’inactivité                    |
| US5  | Ajouter produit       | M        | 3      | Produit ajouté                         |
| US6  | Modifier produit      | S        | 4      | Modification enregistrée               |
| US7  | Supprimer produit     | C        | 4      | Confirmation obligatoire               |
| US8  | Rechercher produit    | S        | 5      | Résultat correct                       |
| US9  | Afficher produits     | S        | 5      | Pagination / tri                       |
| US10 | Filtrer produits      | S        | 4      | Filtres fonctionnels                   |
| US11 | Enregistrer vente     | M        | 5      | Vente valide                           |
| US12 | Mise à jour stock     | M        | 3      | Stock cohérent                         |
| US13 | Générer reçu          | W        | 5      | Reçu conforme                          |
| US14 | Promotions            | W        | 5      | Règles respectées                      |
| US15 | Historique ventes     | W        | 5      | Données exactes                        |
| US16 | Suivi stock           | S        | 5      | Entrées / sorties visibles             |
| US17 | Produits faibles      | M        | 5      | Liste correcte                         |
| US18 | Rapports ventes       | S        | 8      | Export possible                        |
| US19 | Produits populaires   | S        | 8      | Classement exact                       |
| US20 | Chiffre d’affaires    | M        | 8      | Calcul exact                           |

---

## 📌 Planification Agile – 10 Sprints

| Sprint    | Objectif                    | User Stories     | Points |
| --------- | --------------------------- | ---------------- | ------ |
| Sprint 1  | Authentification            | US1              | 3      |
| Sprint 2  | Gestion utilisateurs        | US2, US3         | 7      |
| Sprint 3  | Ajouter / modifier produits | US5, US6         | 7      |
| Sprint 4  | Supprimer / rechercher      | US7, US8         | 9      |
| Sprint 5  | Affichage / filtres         | US9, US10        | 9      |
| Sprint 6  | Gestion ventes              | US11, US12       | 8      |
| Sprint 7  | Reçus & historique          | US13, US15       | 10     |
| Sprint 8  | Promotions & alertes        | US14, US16, US17 | 15     |
| Sprint 9  | Rapports & statistiques     | US18, US19, US20 | 24     |
| Sprint 10 | Tests & optimisation        | Toutes           | —      |

---
