panda-functional-lib
====================

panda-functional-lib er et bibliotek som inneholder kode for funksjonell programmering i Java.

Det ble laget for Panda-prosjektet, men kan brukes av alle.

Hvordan bygge panda-functional-lib
==================================

    mvn clean verify

Hvordan utvikle panda-functional-lib
====================================

Endringer kan pushes rett til develop-branchen. Lag PR til master for review.

Hvordan release panda-functional-lib
====================================

Biblioteket JPL benyttes for å release en ny versjon. Lag pull request fra develop inn i master, så lages det release automatisk. Det benyttes
semantisk versjonering, og versjonen bumpes automatisk med patch-nummeret. For større endringer som ikke er bakoverkompatible: Bump minor eller major
manuelt ved å endre i pom.xml.
