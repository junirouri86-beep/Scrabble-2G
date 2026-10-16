# Scrabble 2G — prototype Android natif

Ce projet est une base de code Kotlin pour Android, pas encore un APK compilé.
Il propose deux joueurs sur le même téléphone, un plateau 15×15, des valeurs de lettres,
un calcul de points et une petite liste de mots de démonstration avec définitions, hors connexion.

ATTENTION : la liste intégrée est minuscule et n'est ni l'ODS officiel ni le dictionnaire Larousse.
Le placement, les bonus, les mots croisés et toutes les règles officielles ne sont pas encore implémentés.
Pour intégrer des dictionnaires complets, il faut une ressource dont la licence permet la redistribution,
et respecter ses conditions d'attribution. L'application ne doit pas prétendre être un produit Larousse.

Pour compiler sur un ordinateur : ouvrez ce dossier dans Android Studio, synchronisez Gradle,
puis choisissez Build > Build APK(s). Le dossier source ne s'installe pas directement sur Android.
