# Kotlin + JPA PoC project

Very simple project used to experiment on Kotlin and JPA integration

## Running it

This project has been developed using [Maven](https://maven.apache.org/) and [Spring Boot](https://projects.spring.io/spring-boot/) and can be executed using:

 > ./mvnw spring-boot:run

By default an in-memory [H2](http://www.h2database.com) database will be used. 

It is also possible to execute it again a [PostgreSQL](https://www.postgresql.org/). In order to do that an Spring Boot profile named ["postgresql"](src/main/resources/application.yml#L8) has been created and can be executed using a command as below:

 > ./mvnw spring-boot:run -Drun.profiles=postgresql

A [docker-compose.yml](docker-compose.yml) is also provided so one can run a PostgreSQL instance in a Docker container executing, for example:

 > docker-compose up --remove-orphans --force-recreate --build

## Using

When project is running system can be accessed using its REST API:

    $ curl http://localhost:8080/people
    {
      "_embedded" : {
        "people" : [ {
          "name" : "Ozzy Osbourne",
          "birthday" : "1948-12-03",
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/people/1"
            },
            "person" : {
              "href" : "http://localhost:8080/people/1"
            }
          }
        }, {
          "name" : "John Lennon",
          "birthday" : "1940-10-09",
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/people/2"
            },
            "person" : {
              "href" : "http://localhost:8080/people/2"
            }
          }
        } ]
      },
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people"
        },
        "profile" : {
          "href" : "http://localhost:8080/profile/people"
        }
      }
    }

A Swagger UI is also available under `/swagger-ui.html` path.
