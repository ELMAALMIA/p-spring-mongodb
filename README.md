# Architecture Spring Boot avec MongoDB Sharded Cluster 

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

Architecture distribuée utilisant MongoDB shardé avec Spring Boot pour gérer efficacement de grandes quantités de données avec une scalabilité horizontale.

##  Article Détaillé

Pour une explication complète de cette architecture, consultez l'article sur Medium :
**[Mise en place d'une Architecture Big Data avec Spring Boot et MongoDB Sharding](https://medium.com/@ayoubelmaalmi/mise-en-place-dune-architecture-big-data-avec-spring-boot-et-mongodb-sharding-94f72fb80666)**

##  Composants

- **Spring Boot Application** (API REST)
- **MongoDB Router** (mongos)
- **Config Servers** (x3) en replica set
- **2 Shards** avec chacun:
    - Primary
    - Secondary 1
    - Secondary 2

<div align="center">
  <img src="public/images/architecture-diagram.png" alt="Architecture Diagram" width="600"/>
  <p><em>Architecture MongoDB Sharded Cluster avec Spring Boot</em></p>
</div>

## Prérequis

- **Docker** + **Docker Compose**
- **Java 17** ou supérieur
- **Maven 3.6+**
- **MongoDB Shell** (mongosh)

##  Installation

1. **Cloner le projet**:
```bash
git clone https://github.com/ELMAALMIA/p-spring-mongodb.git
cd p-spring-mongodb
```

2. **Démarrer le cluster MongoDB**:
```bash
cd config
chmod +x init.sh
./init.sh
```

3. **Démarrer Spring Boot**:
```bash
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

## API Endpoints

### Créer un Produit
```bash
curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-d '{"name":"Product1","region":"EU","price":100,"description":"Sample product"}'
```

### Lister tous les Produits
```bash
curl http://localhost:8080/api/products
```

### Rechercher par Région
```bash
curl http://localhost:8080/api/products?region=EU
```

### Obtenir un Produit par ID
```bash
curl http://localhost:8080/api/products/{id}
```

## Structure du Projet

```
.
├── config/
│   ├── docker-compose.yml    # Configuration Docker MongoDB
│   └── init.sh              # Script d'initialisation
├── src/
│   └── main/
│       ├── java/
│       │   ├── controllers/ # Contrôleurs REST
│       │   ├── models/      # Modèles de données
│       │   ├── repositories/# Repositories Spring Data
│       │   └── services/    # Services métier
│       └── resources/
│           └── application.yml # Configuration Spring
├── pom.xml                  # Dépendances Maven
└── README.md
```

##  MongoDB Sharding

- **Collection shardée**: `products`
- **Clé de sharding**: `region`
- **Réplication**: 1 primary + 2 secondary par shard
- **Distribution**: Automatique basée sur la région

### Configuration du Sharding
```javascript
// Activer le sharding sur la base de données
sh.enableSharding("bigdata")

// Créer un index pour la clé de sharding
db.products.createIndex({ region: 1 })

// Configurer le sharding sur la collection
sh.shardCollection("bigdata.products", { region: 1 })
```

##  Commandes Utiles

### Monitoring du Cluster
```bash
# Status du cluster
docker exec mongodb_router mongosh --eval "sh.status()"

# Vérifier la distribution des données
docker exec mongodb_router mongosh --eval "db.products.getShardDistribution()"

# Statistiques des shards
docker exec mongodb_router mongosh --eval "sh.getShards()"
```

### Gestion des Logs
```bash
# Logs du router
docker logs mongodb_router

# Logs des config servers
docker logs mongodb_configsvr1
docker logs mongodb_configsvr2
docker logs mongodb_configsvr3

# Logs des shards
docker logs mongodb_shard1
docker logs mongodb_shard2
```

### Accès aux Composants
```bash
# Shell MongoDB (via router)
docker exec -it mongodb_router mongosh

# Connexion directe à un shard
docker exec -it mongodb_shard1 mongosh --port 27018

# Vérifier les conteneurs actifs
docker ps
```

### Arrêt et Nettoyage
```bash
# Arrêter le cluster avec suppression des volumes
docker-compose down -v

# Arrêt simple
docker-compose stop

# Redémarrer le cluster
docker-compose restart
```

##  Configuration

### Application Properties
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bigdata
      connection-pool-size: 100
      connect-timeout: 2000ms

server:
  port: 8080

logging:
  level:
    org.springframework.data.mongodb: DEBUG
```

##  Tests

### Lancer les Tests
```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn integration-test

# Test avec profil spécifique
mvn test -Ptest
```

### Test de Performance
```bash
# Test de charge avec Apache Bench
ab -n 1000 -c 10 -H "Content-Type: application/json" \
  -p product.json http://localhost:8080/api/products
```

## Dépannage

### Problèmes Courants

**Le cluster ne démarre pas**:
```bash
# Vérifier les ports
netstat -tulpn | grep :27017
docker ps -a
```

**Problèmes de connexion**:
```bash
# Vérifier la connectivité
docker exec mongodb_router mongosh --eval "db.adminCommand('ping')"
```

**Performance dégradée**:
```bash
# Analyser la distribution des chunks
docker exec mongodb_router mongosh --eval "sh.printShardingStatus()"
```

##  Fonctionnalités

### Avantages du Sharding
- ✅ **Scalabilité horizontale** - Ajout facile de nouveaux shards
- ✅ **Haute disponibilité** - Réplication automatique des données
- ✅ **Performance** - Distribution de charge entre serveurs
- ✅ **Flexibilité** - Adaptation à la croissance des données

### Cas d'Usage
- Applications avec de gros volumes de données
- Systèmes nécessitant une haute disponibilité
- Applications géographiquement distribuées
- Plateformes e-commerce avec catalogues volumineux

## Ressources

- ** Article Medium**: [Architecture Big Data avec Spring Boot et MongoDB](https://medium.com/@ayoubelmaalmi/mise-en-place-dune-architecture-big-data-avec-spring-boot-et-mongodb-sharding-94f72fb80666)
- **Documentation Spring Data MongoDB**: [Spring.io](https://spring.io/projects/spring-data-mongodb)
- **MongoDB Sharding Guide**: [MongoDB Docs](https://docs.mongodb.com/manual/sharding/)
- **Docker Compose Reference**: [Docker Docs](https://docs.docker.com/compose/)

##  Auteur

**AYOUB EL MAALMI**
- Backend Developer
- Spécialisé en REST APIs, microservices, automation et solutions cloud
- GitHub: [@ELMAALMIA](https://github.com/ELMAALMIA)
- Medium: [@ayoubelmaalmi](https://medium.com/@ayoubelmaalmi)

---

**Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile !**

---

*Développé avec  en utilisant Spring Boot, MongoDB et Docker*