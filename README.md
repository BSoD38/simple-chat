# SimpleChat #

SimpleChat est un ensemble de 2 applications permettant de déployer un serveur de chat et un client en Java.

## Besoins fonctionnels ##

L'application doit respecter les besoins suivants :
* Les utilisateurs doivent pouvoir se connecter sur le serveur de chat en même temps
* Un utilisateur doit pouvoir créer une chatroom
* Un utilisateur NE doit PAS pouvoir créer une nouvelle chatroom ayant le même nom qu'une chatroom déjà existante
* Les utilisateurs doivent pouvoir se connecter à une chatroom simultanément
* Les utilisateurs doivent recevoir en temps réel les nouvelles chatroom créées
* Un utilisateur doit pouvoir envoyer un nouveau message sur une chatroom
* Un utilisateur doit recevoir en temps réel les nouveaux messages envoyés dans une chatroom
* Le status des utilisateurs doit être mis à jour en temps réel dans le chat (actif / déconnecté)

## Contraintes techniques ##
* Chaque message doit avoir un ID unique sur la plateforme
* Chaque nom de chatroom doit être unique
* Un utilisateur est identifié par son username

## Manuel utilisateur ##

Le projet SimpleChat utilise un build multi-projet `Gradle`.
Le répertoire est composé de 3 projets :

* Un projet `server` contennant le service du serveur.
* Un projet `clients/console` contenant le service d'un client lourd en Java.
* Un projet `common` contenant le code source partagé par ces 2 services.

Le serveur utilise 2 ports :

* Un port exposant des web services pour les requêtes du client
* Un port ouvrant une socket avec les clients pour gérer les notifications

Le client se connecte donc à ces 2 ports également.

En effet, certains événements demandent un synchronisation entre le serveur et le client :

* Le serveur notifie le client (par la websocket) lors de la création d'une chatroom, d'un message ou lors du changement d'état d'un utilisateur
* Le client se connecte sur le serveur régulièrement (web service de login) pour notifier que l'utilisateur est toujours actif

### Dépendences du serveur ###

Le serveur utilise le micro framework [Spark Java](http://sparkjava.com/) pour ouvrir les webservices.

### Dépendences du client ###

Le client utilise le framework [Lanterna](https://github.com/mabe02/lanterna) pour créer une interface utilisateur sur la console.

Les appels aux web services sont réalisés à travers l'API [Fluent](https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) de la librairie Apache HTTP Components.

### Déployer le serveur en local ###

`gradlew :server:run --args="<port Socket> <port web services>"`

Exemple:

`gradlew :server:run --args="1234 2345"`

### Arrêter le serveur ###

`CTRL + c`

### Déployer le client en local ###

`gradlew :clients:console:run --args="<IP du serveur> <port socket du serveur> <port web services du serveur>"

Exemple:

`gradlew :clients:console:run --args="127.0.0.1 1234 2345"`

## Références ##

### Test framework for Specifications ###
[Spock framework](http://spockframework.org/)

### UI library for the client app ###
[Lanterna](https://github.com/mabe02/lanterna)

### Web server for the server app ###
[Spark Java](http://sparkjava.com/)
