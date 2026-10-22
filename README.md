# 🌍 EcoWatch — Plateforme Intelligente de Surveillance Météorologique et Environnementale

**EcoWatch** est une plateforme intelligente de surveillance météorologique et environnementale développée dans le cadre d'un **Projet de Fin d'Études (PFE)**.

L'objectif du projet est de centraliser et exploiter des données météorologiques et environnementales afin de permettre leur **consultation, leur surveillance et la détection automatique de situations nécessitant une attention particulière**.

La plateforme repose sur une **architecture microservices**, une authentification sécurisée avec **Keycloak**, une interface web développée avec **Angular** et un **assistant conversationnel basé sur l'IA générative**.

---

## 📌 Fonctionnalités principales

### 🌦️ Surveillance météorologique

* Récupération des données météorologiques depuis **OpenWeatherMap**.
* Surveillance de plusieurs villes marocaines.
* Affichage de :
    * Température
    * Humidité
    * Vitesse du vent
    * Description météorologique
* Mise à jour périodique des données.
* Récupération et stockage des prévisions météorologiques.

### 💧 Surveillance des ressources en eau

* Gestion des ressources en eau.
* Suivi de leur niveau actuel.
* Définition de seuils de surveillance.
* Détection des situations nécessitant une intervention.

### 📡 Simulation IoT

Le projet intègre un service IoT permettant de simuler le fonctionnement de capteurs environnementaux.

Les mesures sont générées périodiquement par des tâches planifiées et peuvent être utilisées pour :

* Simuler des relevés de capteurs.
* Stocker les mesures.
* Surveiller l'état des ressources.
* Détecter automatiquement certaines situations anormales.

> Les capteurs utilisés dans ce projet sont simulés logiciellement. Aucun matériel IoT physique n'est nécessaire pour exécuter la plateforme.

### 🚨 Gestion des alertes

Le système permet de détecter et gérer des alertes environnementales. 

Les alertes peuvent être associées à différents niveaux de gravité et leur traitement peut être 
effectué par un utilisateur disposant des droits d'administration.

### 🤖 Assistant intelligent

EcoWatch intègre un assistant conversationnel basé sur **Spring AI** et un modèle fourni par **Groq**.

L'assistant peut interagir avec les données de la plateforme grâce à des outils permettant notamment de consulter :

* Les données météorologiques.
* Les alertes.
* Les ressources en eau.
* Les données environnementales disponibles via les services concernés.

L'objectif est de permettre à l'utilisateur d'interroger la plateforme en langage naturel.

### 🔐 Sécurité

La sécurité est assurée par **Keycloak** et **Spring Security**.

Le système utilise :

* OAuth 2.0
* OpenID Connect
* JWT
* Authorization Code Flow
* PKCE avec S256
* Gestion des rôles

Deux rôles principaux sont utilisés :

* `USER`
* `ADMIN`

Les droits d'accès aux différentes fonctionnalités sont contrôlés en fonction du rôle de l'utilisateur.

---

# 🏗️ Architecture

EcoWatch est basé sur une architecture distribuée composée de plusieurs microservices.

```text
                         ┌──────────────────────┐
                         │      Angular         │
                         │      Frontend        │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      Keycloak        │
                         │ OAuth2 / OIDC / JWT  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     API Gateway      │
                         └──────────┬───────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
     ┌────────────────┐   ┌────────────────┐   ┌────────────────────┐
     │ Weather Service│   │   IoT Service  │   │  AI Agent Service  │ 
     └───────┬────────┘   └───────┬────────┘   └─────────┬──────────┘
             │                    │                      │
             ▼                    ▼                      ▼
     ┌───────────────┐    ┌───────────────┐       ┌──────────────┐
     │  PostgreSQL   │    │  PostgreSQL   │       │    Groq      │
     └───────────────┘    └───────────────┘       └──────────────┘
                                  ▲
                                  │
                         ┌────────┴────────┐
                         │ Data Aggregation│
                         │      BFF        │
                         └─────────────────┘

              ┌─────────────────────────────────────────────┐
              │            Infrastructure                   │
              │                                             │
              │ Discovery Service │ Config Service │ Docker │
              └─────────────────────────────────────────────┘
```

### 🧩 Microservices

| Service                      | Responsabilité                                          |
|------------------------------| ------------------------------------------------------- |
| **API Gateway**              | Point d'entrée de l'application et routage des requêtes |
| **Weather Service**          | Données météorologiques et prévisions                   |
| **IoT Service**              | Capteurs simulés, mesures, ressources en eau et alertes |
| **Data Aggregation Service** | Agrégation des données Weather et IoT                   |
| **AI Agent Service**         | Assistant conversationnel basé sur l'IA                 |
| **Config Service**           | Centralisation de la configuration                      |
| **Discovery Service**        | Découverte des services                                 |
| **Keycloak**                 | Authentification et autorisation                        |

Le **Data Aggregation Service** fonctionne comme un **Backend for Frontend (BFF)**. Il ne possède pas sa propre base de données et récupère les informations nécessaires auprès des services Weather et IoT.

---

# 🗄️ Gestion des données

Le projet suit une approche **Database per Service** pour les microservices qui possèdent des données persistantes.

```text
Weather Service
       │
       ▼
ecowatch_weather_db


IoT Service
       │
       ▼
ecowatch_iot_db
```

Les bases de données sont développées avec **PostgreSQL** et exploitées avec :

* Spring Data JPA
* Hibernate
* PostgreSQL

---

# 🛠️ Technologies utilisées

## Backend

* Java 21
* Spring Boot
* Spring Cloud
* Spring Security
* Spring Data JPA
* Hibernate
* OpenFeign
* Spring AI
* Maven

## Architecture distribuée

* Spring Cloud Gateway
* Eureka Server
* Spring Cloud Config Server
* Microservices
* Backend for Frontend (BFF)

## Frontend

* Angular
* TypeScript
* Bootstrap
* Leaflet

## Sécurité

* Keycloak
* OAuth 2.0
* OpenID Connect
* JWT
* PKCE

## Intelligence artificielle

* Spring AI
* Groq
* LLM avec appels d'outils

## Données et APIs

* PostgreSQL
* OpenWeatherMap API
* REST API

## Dev / Infrastructure

* Docker et Docker Compose
* nginx (service du frontend)
* GitHub Actions (CI/CD)
* GitHub Container Registry (GHCR)
* Git
* GitHub
* IntelliJ IDEA
* Postman
* OpenAPI / Swagger
* Spring Boot Actuator

---

# 🚀 Installation et exécution

## 1. Cloner le projet

```bash
git clone -b dev https://github.com/MOKDAD2107/PFE--Soutenance-.git
cd PFE--Soutenance-
```

---

# 🔐 Configuration des variables d'environnement

Les clés API et informations sensibles ne sont pas stockées dans Git.

Créer un fichier `.env`

Exemple :

```env
OPENWEATHER_API_KEY=your_openweathermap_api_key
GROQ_API_KEY=your_groq_api_key
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=your_postgresql_password
```
> Avec Docker Compose, le fichier `.env` de la racine est lu automatiquement. Variables utilisées : `OPENWEATHER_API_KEY` (weather-service), `GROQ_API_KEY` (ai-agent-service) et `POSTGRES_PASSWORD`.
---

# 🔑 Configuration Keycloak

> **Avec Docker**, le realm `ecowatch` est importé automatiquement au démarrage depuis `docker/keycloak/ecowatch-realm.json`. Cet import **ne contient pas d'utilisateur** : voir « Créer un utilisateur » dans la section Docker ci-dessous.

Créer un realm Keycloak :

```text
ecowatch
```

Configurer le client utilisé par l'application Angular ainsi que les rôles :

```text
USER
ADMIN
```

Le backend utilise le realm :

```text
http://localhost:8080/realms/ecowatch
```

Les services sécurisés utilisent JWT pour vérifier l'identité et les autorisations des utilisateurs.

---

# ▶️ Démarrage du projet

## 🐳 Avec Docker (recommandé)

Aucune installation de Java, Maven, Node.js, PostgreSQL ou Keycloak n'est nécessaire : seul **Docker Desktop** est requis, avec plusieurs Go de RAM disponibles (8 Go recommandés). Le **premier démarrage prend 4 à 5 minutes**, car les services attendent que le Config Server soit prêt.

La configuration des services est lue au démarrage depuis le dépôt [`config-ecowatch`](https://github.com/MOKDAD2107/config-ecowatch) (branche `dev`) : une connexion Internet est nécessaire.

### Option A — à partir des images publiées sur GHCR

Aucune compilation locale : les images sont téléchargées depuis GitHub Container Registry.

```bash
docker compose -f docker-compose.deploy.yml pull
docker compose -f docker-compose.deploy.yml up -d
```

Pour figer une version précise des images, ajouter `TAG=<identifiant-du-commit>` devant la commande `up`.

### Option B — construction locale des images

```bash
docker compose up -d --build
```

### Vérification

```bash
docker compose ps
```

Tous les services doivent être `Up` (`healthy` pour Eureka, PostgreSQL et le Config Server).

| Interface | Adresse |
|---|---|
| Application | http://localhost:4200 |
| Eureka | http://localhost:8761 |
| Console Keycloak | http://localhost:8080 |

| Service | Port |
|---|---|
| Frontend Angular (nginx) | 4200 |
| API Gateway | 8888 |
| Weather Service | 8082 |
| IoT Service | 8083 |
| Data Aggregation Service | 8084 |
| AI Agent Service | 8087 |
| Config Service | 9999 |
| Discovery Service (Eureka) | 8761 |
| Keycloak | 8080 |
| PostgreSQL | 5432 |

### Créer un utilisateur

Dans la console Keycloak (les identifiants d'administration définis dans le compose sont réservés au développement) :

1. Sélectionner le realm **ecowatch**, puis **Users**, puis **Add user**.
2. Onglet **Credentials** : définir un mot de passe (option **Temporary** désactivée).
3. Onglet **Role mapping** : attribuer le rôle `USER` ou `ADMIN`.

Se connecter ensuite sur http://localhost:4200.

### Arrêt

```bash
docker compose down
```

> N'ajoutez **pas** l'option `-v` : elle supprime les volumes, donc les données PostgreSQL et la configuration Keycloak.

---

# 🧪 Tests

Les APIs peuvent être testées avec :

* Postman
* IntelliJ HTTP Client
* Swagger / OpenAPI

Les fonctionnalités principales testées comprennent :

* Authentification
* Autorisation par rôle
* Récupération des données météorologiques
* Prévisions météorologiques
* Gestion des capteurs
* Gestion des ressources en eau
* Génération et gestion des alertes
* Agrégation des données
* Interaction avec l'assistant IA

---

# 📊 Fonctionnement des tâches planifiées

Plusieurs tâches planifiées permettent d'actualiser automatiquement les données.

### Weather Service

```text
WeatherScheduler
        │
        └── Mise à jour périodique de la météo

WeatherPrevisionScheduler
        │
        └── Mise à jour périodique des prévisions
```

### IoT Service

```text
IotSensorScheduler
        │
        └── Simulation des mesures des capteurs

WaterRessourceScheduler
        │
        └── Surveillance des ressources en eau

AlertScheduler
        │
        └── Détection / traitement des alertes
```

---

# 🤖 Fonctionnement de l'assistant IA

Le fonctionnement général est le suivant :

```text
Utilisateur
     │
     ▼
Angular
     │
     ▼
AI Agent Service
     │
     ▼
Groq / LLM
     │
     ├── Weather Tool ──────► Weather Service
     │
     ├── Alert Tool ────────► IoT Service
     │
     └── Water Tool ────────► IoT Service
     │
     ▼
Réponse générée
     │
     ▼
Utilisateur
```

L'assistant ne repose pas sur une base documentaire RAG dans cette version. Il interroge directement les données disponibles dans la plateforme via des outils.

---
# 📰 Les interfaces de la plateforme

### 🔐 Interface d'authentification avec Keycloak : 

<img src="imgs/img.png">

### 📊 Tableau de bord de la plateforme : 

<img src="imgs/img_1.png">

### 🗺️ Vue cartographique des données environnementales :
<img src="imgs/img_2.png">

### 💧 Interface de suivi des ressources en eau pour un utilisateur ADMIN :

<img src="imgs/img_3.png">

### 🚨 Interface de consultation des alertes environnementales pour un utilisateur ADMIN :

<img src="imgs/img_4.png">

### 🤖 Interface de l'assistant conversationnel

<img src="imgs/img_5.png">


---

# 📁 Structure du projet

```text
PFE--Soutenance-/
│
├── .github/workflows/          
│
├── docker/
│   ├── keycloak/               
│   └── postgres/init.sql       
│
├── config-service/
├── discovery-service/
├── gateway-service/
├── weather-service/
├── iot-service/
├── data-aggregation-service/
├── ai-agent-service/
├── ecowatch-frontend/
│
├── imgs/
├── docker-compose.yml          
├── docker-compose.deploy.yml   
├── .env.example
├── .gitignore
└── README.md
```

La configuration centralisée des services (`application.properties`, `WEATHER-SERVICE.properties`, etc.) se trouve dans un dépôt séparé : [`config-ecowatch`](https://github.com/MOKDAD2107/config-ecowatch).

---

# 👨‍💻 Auteur

**Mohamed Mokdad**

Projet de Fin d'Études — Master Ingénierie Informatique
**Big Data & Cloud Computing (II-BDCC)**
ENSET Mohammedia
Année universitaire **2025–2026**

---

