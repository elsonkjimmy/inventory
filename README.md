📘 Document d’Analyse des Besoins – Système de Gestion de Stock
1️⃣ Contexte du projet

Le projet consiste à développer un Système de Gestion de Stock pour une entreprise. L’objectif est de :

Automatiser les ventes et la gestion des produits

Réduire les erreurs, fraudes et pertes

Fournir des rapports et statistiques fiables

Assurer la sécurité des données et des accès

Offrir une interface simple et réactive

L’application sera locale mais pourra envoyer des mails et notifications. Le projet est destiné à une équipe de 2 personnes et prévu sur 3 jours.

2️⃣ Objectifs principaux

Permettre aux utilisateurs de se connecter selon leur rôle (Admin ou Gestionnaire)

Gérer les produits et catégories (CRUD)

Suivre les ventes et générer des reçus

Fournir des alertes et notifications pour les stocks faibles

Générer des rapports détaillés sur les ventes et produits

Garantir sécurité, performance et simplicité d’utilisation

3️⃣ Acteurs
Acteur	Description	Actions principales
Administrateur	Gère l’ensemble du système	Ajouter/modifier/supprimer utilisateurs et produits, générer promotions, consulter tableau de bord général, consulter rapports, effectuer des ventes si nécessaire
Gestionnaire	Utilisateur métier	Effectuer ventes, gérer produits (ajouter/modifier/rechercher), consulter tableau de bord personnel, générer reçus, suivre stock
Client	Personne qui achète	Payer un produit, recevoir un reçu
4️⃣ Besoins Fonctionnels
ID	Besoin Fonctionnel	Description
BF1	Connexion utilisateur	Se connecter avec login/mot de passe selon rôle
BF2	Gestion utilisateurs	L’admin peut créer, modifier, supprimer des utilisateurs
BF3	Ajouter produit	Ajouter produit avec nom, catégorie, prix, quantité, seuil d’alerte
BF4	Modifier produit	Modifier informations d’un produit existant
BF5	Supprimer produit	Supprimer un produit avec confirmation
BF6	Rechercher produit	Rechercher par nom ou catégorie
BF7	Filtrer produits	Filtrer par catégorie, prix, stock, fournisseur
BF8	Enregistrer vente	Enregistrer la vente avec produits, quantité, prix
BF9	Mise à jour automatique du stock	Déduire automatiquement la quantité vendue
BF10	Générer reçu	Générer un reçu (PDF/imprimable) après vente
BF11	Gestion promotions	L’admin peut appliquer des promotions et réductions
BF12	Historique ventes	Consulter l’historique des ventes par période
BF13	Alertes stock faible	Notification lorsque le stock atteint le seuil minimum
BF14	Rapports	Produits en stock faible, ventes par période, produits les plus vendus, chiffre d’affaires
5️⃣ Besoins Non-Fonctionnels
ID	Description
BNF1	Sécurité
BNF2	Performance
BNF3	Disponibilité
BNF4	Simplicité
BNF5	Notifications
BNF6	Reporting
BNF7	Multi-utilisateurs
6️⃣ Règles Métiers

Seul l’admin peut ajouter, supprimer ou modifier des utilisateurs et lancer les promotions

Chaque employé a accès uniquement à son tableau de bord

Toutes les ventes doivent générer une facture

Les produits en dessous du seuil critique déclenchent une notification

Les produits périmés ne peuvent pas être vendus

Les produits proches de la péremption peuvent être automatiquement mis en promotion

Seul l’admin valide les ajouts de produits

7️⃣ User Stories (Agile)
ID	User Story	MoSCoW	Story Points	Critères d’acceptabilité
US1	En tant qu’utilisateur, je veux me connecter avec login/mot de passe afin d’accéder au système	M	3	Login correct → accès autorisé ; sinon → message d’erreur
US2	En tant qu’admin, je veux distinguer Admin et Gestionnaire pour gérer les droits	M	2	Rôle assigné correctement, droits appliqués selon rôle
US3	En tant qu’admin, je veux créer/modifier/supprimer des utilisateurs afin de gérer l’équipe	M	5	Utilisateur ajouté/modifié/supprimé avec confirmation
US4	En tant que système, je veux déconnecter automatiquement après 30 min d’inactivité pour sécuriser les comptes	S	2	Inactivité détectée → déconnexion automatique
US5	En tant qu’admin/gestionnaire, je veux ajouter un produit afin de gérer le stock	M	3	Produit ajouté correctement, stock mis à jour
US6	Modifier un produit	S	4	Modification enregistrée pour produit existant
US7	Supprimer un produit	C	4	Confirmation obligatoire, produit supprimé et stock mis à jour
US8	Rechercher un produit	S	5	Produit trouvé → affiché ; produit absent → message
US9	Afficher tous les produits	S	5	Tous les produits visibles avec tri/pagination
US10	Filtrer produits	S	4	Filtres corrects appliqués
US11	Enregistrer une vente	M	5	Vente enregistrée avec produits, quantité, prix
US12	Mise à jour automatique du stock	M	3	Stock mis à jour après vente
US13	Générer un reçu	W	5	Reçu PDF ou imprimable avec infos correctes
US14	Appliquer promotions	W	5	Critères respectés : stock suffisant, dates, prix réduit
US15	Historique ventes	W	5	Historique exact, tri par date ou produit
US16	Suivi mouvements stock	S	5	Toutes les entrées/sorties visibles
US17	Rapports produits faibles	M	5	Produits sous seuil listés correctement
US18	Rapports ventes par période	S	8	Période sélectionnable, exportable
US19	Rapports produits les plus vendus	S	8	Classement exact par période
US20	Chiffre d’affaires total	M	8	Calcul exact par période
📌 Planification Agile – 10 Sprints
Sprint	Objectif principal	User Stories assignées	MoSCoW	Story Points
Sprint 1	Mise en place de l’environnement et connexion	US1 : Connexion	M	3
Sprint 2	Gestion des utilisateurs	US2 : Distinction rôles
US3 : CRUD utilisateurs	M
M	2
5
Sprint 3	Ajouter et modifier les produits	US5 : Ajouter produit
US6 : Modifier produit	M
S	3
4
Sprint 4	Supprimer et rechercher produits	US7 : Supprimer produit
US8 : Rechercher produit	C
S	4
5
Sprint 5	Affichage et filtrage des produits	US9 : Afficher tous les produits
US10 : Filtrer produits	S
S	5
4
Sprint 6	Gestion des ventes	US11 : Enregistrer vente
US12 : Mise à jour stock automatique	M
M	5
3
Sprint 7	Génération des reçus et historique ventes	US13 : Générer reçu
US15 : Historique ventes	W
W	5
5
Sprint 8	Gestion promotions et alertes stock	US14 : Appliquer promotions
US16 : Suivi mouvements stock
US17 : Rapports produits faibles	W
S
M	5
5
5
Sprint 9	Rapports ventes et produits	US18 : Rapports ventes par période
US19 : Produits les plus vendus
US20 : Chiffre d’affaires total	S
S
M	8
8
8
Sprint 10	Tests finaux, optimisation et corrections	Toutes les fonctionnalités	Toutes	Toutes
📌 Explication de la planification

Sprint 1 : Configuration initiale + authentification de base (login/logout).

Sprint 2 : Gestion utilisateurs, distinction des droits entre Admin et Gestionnaire.

Sprint 3 à 5 : Gestion complète des produits (CRUD + recherche + filtre).

Sprint 6 à 7 : Gestion des ventes et génération de reçus, mise à jour automatique des stocks, historique.

Sprint 8 : Promotions, alertes, suivi des mouvements de stock et produits faibles.

Sprint 9 : Rapports complets sur ventes, produits et chiffre d’affaires.

Sprint 10 : Tests finaux, corrections, optimisation et validation globale.

✅ Conseils pour les sprints

Chaque sprint dure environ 0,5 à 1 jour pour ton projet de 3 jours et 2 développeurs.

Les user stories sont priorisées par MoSCoW et Story Points pour planifier la charge.

Tester chaque sprint avant de passer au suivant.

Documenter les feedbacks et bugs dans le sprint 10 pour corrections finales.
