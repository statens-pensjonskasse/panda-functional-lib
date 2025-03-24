panda-functional-lib
====================

panda-functional-lib er et bibliotek som inneholder kode for funksjonell programmering i Java.

Det ble laget for Panda-prosjektet, men kan brukes av alle.

Hvordan bygge panda-functional-lib
==================================

    mvn clean verify

Hvordan utvikle panda-functional-lib
====================================

Endringer kan pushes rett til develop-branchen. Lag PR til main for review.

Hvordan release panda-functional-lib
====================================

Vi bruker en github-workflow for å lage nye releaser. Lag pull request fra develop inn i main, så lages det release automatisk. 
Det benyttes semantisk versjonering, og versjonen bumpes automatisk med patch-nummeret.
For større endringer som ikke er bakoverkompatible: Bump minor eller major manuelt ved å endre i pom.xml.
