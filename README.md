# JobOffers

## Functionalities of the Job Offers Application
- Registration
- Authorization by token
- Viewing offers
- Adding offers
- Scheduler is fetching offers every 3 hours
- Request validation


## Tech

JobOffers is developed using following technologies: <br>

Core: <br>
![image](https://img.shields.io/badge/17-Java-orange?style=for-the-badge) &nbsp;
![image](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) &nbsp;
![image](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring) &nbsp;
![image](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white) &nbsp;
![image](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white) &nbsp;
![image](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white) &nbsp;

Testing:<br>
![image](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white) &nbsp;
![image](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge) &nbsp;
![image](https://img.shields.io/badge/Testcontainers-9B489A?style=for-the-badge) &nbsp;


## Installation and run

### Requirements:
- Docker

### To run the application:
- Just run following command, and wait for containers to be pulled up and started.

``
docker compose up
``

- Alternatively you can run docker-compose file through you IDE

After everything builds and ready, you can start application and test using [Postman](https://www.postman.com/)
or use <a href="http://localhost:8080/swagger-ui/index.html#/">Swagger</a>.


## Rest-API Endpoints

Service url: http://localhost:8080

| HTTP METHOD | Endpoint           | Action                                      |
|-------------|--------------------|---------------------------------------------|
| GET         | /offers            | Retrieve all available offers               |
| GET         | /offers/{id}       | Retrieve an offer for a given ID            |
| POST        | /offers            | Add new offer                               |
| POST        | /register          | Register a new user                         |
| POST        | /token             | Generate an access token with login details |
