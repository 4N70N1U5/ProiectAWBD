# Skybase - Airline management application using Spring Boot

Nume: Nitoi Antonio

Grupa: 406 - IS

# Descriere

Proiectul reprezintă o aplicație de management al resurselor pentru companii aeriene, ce permite gestionarea informațiilor despre angajații companiei, job-urile pe care lucrează aceștia și departamentele din care fac parte aceste job-uri, aeronavele companiei și zborurile pe care compania le efectuează. De asemenea, pot fi gestionate aeroporturile din care compania efectuează zboruri, împreună cu orașele si țările în care se află acestea. Zborurile au un aeroport din care pleacă si un aeroport destinație si se repeta periodic, fiind efectuate de diverse aeronave si echipaje, in funcție de ziua în care are loc zborul. Astfel, aplicația permite alocarea pe zboruri a aeronavelor și a echipajelor.

# Business Requirements

1. Sistemul trebuie sa permita gestionarea aeroporturilor, oraselor si tarilor din care compania aeriana efectueaza zboruri.
2. Sistemul trebuie sa permita gestionarea departamentelor companiei si a job-urilor din cadrul acestora.
3. Sistemul trebuie sa permita gestionarea angajatilor companiei.
4. Sistemul trebuie sa permita gestionarea aeronavelor pe care le detine compania.
5. Sistemul trebuie sa permita gestionarea zborurilor efectuate de companie.
6. Utilizatorul trebuie sa poata vizualiza angajatii disponibili intr-o anumita data / disponibilitatile unui angajat.
7. Utilizatorul trebuie sa poata vizualiza aeronavele disponibile intr-o anumita data / disponibilitatile unei aeronave.
8. Sistemul trebuie sa permita alocarea pe un zbor a unei aeronave si a unui echipaj doar daca acestea sunt disponibile in acea data, altfel sa afiseze o eroare.
9. Sistemul trebuie sa permita alocarea pe un zbor a unei aeronave doar daca are autonomia necesara pentru acel zbor si a unui angajat doar daca face parte din departamentele de echipaj de zbor sau personal de cabina.
10. Sistemul trebuie sa permita vizualizarea alocarilor existente ale angajatilor si aeronavelor.

# Features pentru stadiul de MVP

1. CRUD pentru aeroporturi, orase, tari si zboruri.
2. CRUD pentru departamente si job-uri.
3. CRUD pentru resurse (angajati si aeronave).
4. Identificarea si vizualizarea disponibilitatilor angajatilor si aeronavelor.
5. CRUD pentru alocari si validarea disponibilitatii si potrivirii resurselor inainte de a salva alocarea acestora pe un zbor.

# Necesitati pentru a rula proiectul

Este necesara versiunea 21 de Java, Maven si Docker Desktop pentru a putea rula proiectul. Testele pot fi rulate folosind comanda ```mvn test```.

# Documentatie API

API-ul permite gestionarea resurselor companiei aeriene, stocate intr-o baza de date MySQL.

Pe ruta ```/swagger-ui/index.html``` se poate accesa documentatia Swagger a API-ului, care ofera o interfata grafica pentru testarea endpoint-urilor si detalii despre ce parametri trebuie sa includa fiecare request, cum trebuie sa arate body-ul acestora si ce raspunsuri pot intoarce.

API-ul include urmatoarele endpoint-uri:

### Tari

Acest controller permite gestionarea tarilor si defineste urmatoarele endpoint-uri:

1. **GET /countries** - returneaza toate tarile
2. **GET /countries/{id}** - returneaza tara cu id-ul specificat
3. **POST /countries** - adauga o tara
4. **PUT /countries/{id}** - modifica tara cu id-ul specificat
5. **DELETE /countries/{id}** - sterge tara cu id-ul specificat

### Orase

Acest controller permite gestionarea oraselor. Orasele depind de existenta tarilor. Defineste urmatoarele endpoint-uri:

1. **GET /cities** - returneaza toate orasele
2. **GET /cities/{id}** - returneaza orasul cu id-ul specificat
3. **POST /cities** - adauga un oras
4. **PUT /cities/{id}** - modifica orasul cu id-ul specificat
5. **DELETE /cities/{id}** - sterge orasul cu id-ul specificat

### Aeroporturi

Acest controller permite gestionarea aeroporturilor. Aeroporturile depind de existenta oraselor. Defineste urmatoarele endpoint-uri:

1. **GET /airports** - returneaza toate aeroporturile
2. **GET /airports/{id}** - returneaza aeroportul cu id-ul specificat
3. **POST /airports** - adauga un aeroport
4. **PUT /airports/{id}** - modifica aeroportul cu id-ul specificat
5. **DELETE /airports/{id}** - sterge aeroportul cu id-ul specificat

### Departamente

Acest controller permite gestionarea departamentelor si defineste urmatoarele endpoint-uri:

1. **GET /departments** - returneaza toate departamentele
2. **GET /departments/{id}** - returneaza departamentul cu id-ul specificat
3. **POST /departments** - adauga un departament
4. **PUT /departments/{id}** - modifica departamentul cu id-ul specificat
5. **DELETE /departments/{id}** - sterge departamentul cu id-ul specificat

### Job-uri

Acest controller permite gestionarea job-urilor (pozitiilor din companie). Job-urile depind de existenta departamentelor. Defineste urmatoarele endpoint-uri:

1. **GET /jobs** - returneaza toate job-urile
2. **GET /jobs/{id}** - returneaza job-ul cu id-ul specificat
3. **POST /jobs** - adauga un job
4. **PUT /jobs/{id}** - modifica job-ul cu id-ul specificat
5. **DELETE /jobs/{id}** - sterge job-ul cu id-ul specificat

### Angajati

Acest controller permite gestionarea angajatilor. Angajatii depind de existenta job-uri. Defineste urmatoarele endpoint-uri:

1. **GET /employees** - returneaza toti angajatii
2. **GET /employees/{id}** - returneaza angajatul cu id-ul specificat
3. **POST /employees** - adauga un angajat
4. **PUT /employees/{id}** - modifica angajatul cu id-ul specificat
5. **DELETE /employees/{id}** - sterge angajatul cu id-ul specificat

### Aeronave

Acest controller permite gestionarea aeronavelor si defineste urmatoarele endpoint-uri:

1. **GET /aircrafts** - returneaza toate aeronavele
2. **GET /aircrafts/{id}** - returneaza aeronava cu id-ul specificat
3. **POST /aircrafts** - adauga o aeronava
4. **PUT /aircrafts/{id}** - modifica aeronava cu id-ul specificat
5. **DELETE /aircrafts/{id}** - sterge aeronava cu id-ul specificat

### Zboruri

Acest controller permite gestionarea zborurilor efectuate de companie. Zborurile depind de existenta aeroporturilor. Defineste urmatoarele endpoint-uri:

1. **GET /flights** - returneaza toate zborurile
2. **GET /flights/{id}** - returneaza zborul cu id-ul specificat
3. **POST /flights** - adauga un zbor
4. **PUT /flights/{id}** - modifica zborul cu id-ul specificat
5. **DELETE /flights/{id}** - sterge zborul cu id-ul specificat

### Alocari angajati

Acest controller permite gestionarea alocarilor angajatilor pe zboruri. Alocarile depind de existenta angajatilor si a zborurilor. Defineste urmatoarele endpoint-uri:

1. **GET /employee-assignments** - returneaza toate alocarile angajatilor
2. **GET /employee-assignments/{employeeId}/{flightId}/{date}** - returneaza alocarea cu id-ul specificat (format din trei parametri)
3. **GET /employee-assignments/by-date?date** - returneaza alocarile din data specificata
4. **GET /employee-assignments/by-employee?employeeId?startDate?endDate** - returneaza alocarile angajatului cu id-ul specificat intre datele specificate
5. **GET /employee-assignments/by-flight?flightId?startDate?endDate** - returneaza alocarile pe zborul cu id-ul specificat intre datele specificate
6. **POST /employee-assignments** - adauga o alocare
7. **PUT /employee-assignments/{employeeId}/{flightId}/{date}** - modifica alocarea cu id-ul specificat (format din trei parametri)
8. **DELETE /employee-assignments/{employeeId}/{flightId}/{date}** - sterge alocarea cu id-ul specificat (format din trei parametri)

### Alocari aeronave

Acest controller permite gestionarea alocarilor aeronavelor pe zboruri. Alocarile depind de existenta aeronavelor si a zborurilor. Defineste urmatoarele endpoint-uri:

1. **GET /aircraft-assignments** - returneaza toate alocarile aeronavelor
2. **GET /aircraft-assignments/{aircraftId}/{flightId}/{date}** - returneaza alocarea cu id-ul specificat (format din trei parametri)
3. **GET /aircraft-assignments/by-date?date** - returneaza alocarile din data specificata
4. **GET /aircraft-assignments/by-aircraft?aircraftId?startDate?endDate** - returneaza alocarile aeronavei cu id-ul specificat intre datele specificate
5. **GET /aircraft-assignments/by-flight?flightId?startDate?endDate** - returneaza alocarile pe zborul cu id-ul specificat intre datele specificate
6. **POST /aircraft-assignments** - adauga o alocare
7. **PUT /aircraft-assignments/{aircraftId}/{flightId}/{date}** - modifica alocarea cu id-ul specificat (format din trei parametri)
8. **DELETE /aircraft-assignments/{aircraftId}/{flightId}/{date}** - sterge alocarea cu id-ul specificat (format din trei parametri)
