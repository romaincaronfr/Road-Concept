# Road Concept - A powerfull road traffic simulator !
![Powered by LannionTech](./misc/LannionTech.png)

> Projet de génie logiciel en 3ème année de formation IMR à l'ENSSAT Lannion.

## Pré-requis au projet
Pour pouvoir compiler et éxécuter le projet en local sur votre poste, les dépendances suivantes sont nécéssaires :
* [Oracle JDK8] - Le simulateur ainsi que l'API permettant d'y accéder sont développés en Java
* [Maven] - Ces deux projets utilisent le gestionaire de projet Maven
* [Docker] - 
* [Docker-compose] - 

## Comment déployer localement le projet ?

Récupérer le projet à l'aide du dépôt git hébergé sur GitHub
```sh
$ git clone git@github.com:Webrom/Road-Concept.git
```
Se placer dans le dossier ```Docker```
```sh
$ cd Docker/
```
Builder les images des services :
```sh
$ docker-commpose build
```

Lancer les services en conteneurs :
```sh
$ docker-commpose up
```

Cette commande va lalancer les conteneurs suivants :
* Un serveur ```mongodb```, écoutant sur le port ```"27017```
* Un serveur ```postgresql```, écoutant sur le port ```5432```
* Un serveur HTTP ```Vert.x```, écoutant sur le port ```8080```
    
[Documentation API] La documentation de l'API est disponible sur ```http://localhost:8080/doc```

L'administrateur par défaut de l'application est :
```
admin@enssat.fr
admin
```

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [Maven]: <https://maven.apache.org/>
   [Oracle JDK8]: <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>
   [MongoDB]: <https://www.mongodb.com/>
   [Documentation API]: <http://localhost:8080/doc/>
