

Thema
Het gekozen thema voor dit project is takenbeheer. De applicatie stelt gebruikers in staat om taken aan te maken, toe te wijzen aan teams en teamleden, en notificaties te ontvangen via e-mail. Hierbij maken we gebruik van verschillende microservices om de functionaliteiten te verdelen en schaalbaarheid te garanderen.
 
1.	Verwachting
Dit project streeft naar het ontwikkelen van een veerkrachtige en schaalbare microservices-backend voor takenbeheer. Het uiteindelijke doel is een systeem dat teams effectief kan organiseren, taken beheren, toewijzen, en notificaties verzenden via e-mail. Belangrijk hierbij is de integratie van OAuth2 authenticatie, solide beveiligingsmaatregelen en schaalbaarheidsoplossingen voor een betrouwbare applicatie.

2.	Technische Vereisten en Functionaliteiten
2.1.1.	Team-service
2.1.1.1.	Entiteiten
Team, Member, Department
2.1.1.2.	Functionaliteiten
Teamcreatie en -beheer
Dynamisch toevoegen van teamleden op basis van departement
Geautomatiseerde notificaties via e-mail bij het toevoegen van leden
2.1.1.3.	Database
MySQL

2.1.2.	Task-service
2.1.2.1.	Entiteiten
Task
2.1.2.2.	Functionaliteiten
Aanmaken, raadplegen, bijwerken en verwijderen van taken
2.1.2.3.	Database
Mongodb

2.1.3.	Assignment-service
2.1.3.1.	Entiteiten
TaskAssignment
2.1.3.2.	Functionaliteiten
Toewijzen van teamleden aan specifieke taken
Dynamisch bewerken, raadplegen en verwijderen van toewijzingen
2.1.3.3.	Database
Mongodb

2.1.4.	Email-service
2.1.4.1.	Entiteiten
TaskAssignment
2.1.4.2.	Functionaliteiten
Toewijzen van teamleden aan specifieke taken
Dynamisch bewerken, raadplegen en verwijderen van toewijzingen
2.1.4.3.	Database
Mongodb





2.1.5.	API Gateway
Functie:
Beheer van API-aanroepen en routing naar de juiste microservice
Implementatie van Google OAuth2 authenticatie voor beveiligde toegang.

2.1.5.1.	Backend en Taal
Spring Boot voor het ontwikkelen van veerkrachtige microservices.

2.1.5.2.	Database
MySQL voor de Team-service en Email-service
MongoDB voor de Task-service en Assignment-service

2.1.5.3.	Containerisatie
Docker voor het verpakken van elke microservice in een container


3.	Docker en Docker-Compose
Docker:
Containerisatie van elke microservice voor eenvoudige distributie en uitvoering
Mogelijkheid tot het bouwen van een Docker-image voor elke service

Docker-Compose:
Definitie van services, netwerken en volumes in een compose-bestand
Eenvoudige implementatie en schaling van de volledige applicatie met één enkele opdracht

4.	Authenticatie en Unit tests
Integratie van OAuth2 authenticatie voor API-toegang
Robuuste encryptie van gevoelige gegevens in databases

Team-service:
Unit tests voor het correct toevoegen en beheren van teams en leden
Task-service:
Unit tests voor het beheren van taken, inclusief aanmaken, bijwerken en verwijderen
Assignment-service:
Unit tests voor het correct toewijzen en beheren van taken aan teamleden
Email-service:
Unit tests voor het correct verzenden van notificaties via e-mail
API Gateway:
Unit tests voor het correct routeren van aanroepen en authenticatie via OAuth2
 

youtube: https://studio.youtube.com/video/sezySsuXZPE/edit

github: https://github.com/murrelvenlo/tasks-microservices

GitHub Actions:

![gha1.png](..%2Fverslag%2Fimages-request%2Fgha1.png)
 
![gha2.png](..%2Fverslag%2Fimages-request%2Fgha2.png)

![gha3.png](..%2Fverslag%2Fimages-request%2Fgha3.png)

![gha4.png](..%2Fverslag%2Fimages-request%2Fgha4.png)

Requests

Create task:

![Alt Text](../../Project/verslag/images-request/create-task.png)

Get all tasks:

![Alt Text](../../Project/verslag/images-request/get-all-tasks.png)

Get task by taskCode:

![Alt Text](../../Project/verslag/images-request/get%20task%20by%20code.png)



Update task:

![Alt Text](../../Project/verslag/images-request/update%20task.png)



Delete task:

![Alt Text](../../Project/verslag/images-request/delete%20tasks.png)

 

Create team:

![Alt Text](../../Project/verslag/images-request/create%20team.png)

 

Get all teams:

![Alt Text](../../Project/verslag/images-request/get%20all%20team.png)

 

Update team:

![Alt Text](../../Project/verslag/images-request/update%20team.png)

Create member:

![Alt Text](../../Project/verslag/images-request/create%20member.png)

Get all member:

![Alt Text](../../Project/verslag/images-request/get%20all%20members.png)


Update member:

![Alt Text](../../Project/verslag/images-request/update%20member.png)

Delete member:

![delete-member.png](..%2Fverslag%2Fimages-request%2Fdelete-member.png)

Get member by rNumber:

![member-by-rnumber.png](..%2Fverslag%2Fimages-request%2Fmember-by-rnumber.png)

Get all Assignments:

![Alt Text](../../Project/verslag/images-request/get%20all%20assignments.png)


 
