felles-functional-lib
=====================

felles-functional-lib er et bibliotek som inneholder kode for funksjonell programmering i Java.

Det ble laget for Panda-prosjektet, men kan brukes av alle.

Hvordan bygge felles-functional-lib
===================================

    mvn clean verify

Hvordan utvikle felles-functional-lib
=====================================

Modulen har tradisjonelt vært eid av Team Premie, men nå ligger det i felles-prosjektet i Git. Derfor kan alle utvikle og endre kode i denne modulen,
men det hadde vært fint om Team Premie ble satt som reviewers i PRen, dersom du gjør endringer. Endringer kan pushes rett til develop-branchen. Lag PR
til master for review.

Hvordan release felles-functional-lib
=====================================

Biblioteket JPL benyttes for å release en ny versjon. Lag pull request fra develop inn i master, så lages det release automatisk. Det benyttes
semantisk versjonering, og versjonen bumpes automatisk med patch-nummeret. For større endringer som ikke er bakoverkompatible: Bump minor eller major
manuelt ved å endre i pom.xml.
