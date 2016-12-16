# eGym Recruiting Coding Task

**Do not share this project or its solution with people outside of eGym GmbH!**

## Technologies used in the project

* Oracle/Open JDK 1.7 or 1.8 (must be pre-installed).
* [Gradle](http://gradle.org/) for build automation.
* [Guice](https://github.com/google/guice) for dependency injection.
* [JPA/Hibernate](http://hibernate.org/orm/) for persistence.
* [Jersey](https://jersey.java.net/) for REST endpoints.
* [Swagger UI](https://github.com/swagger-api/swagger-ui) for manual API testing and API documentation.
* [JUnit](http://junit.org/junit4/) for unit testing.
* [Mockito](http://mockito.org/) for mocked testing.
* [Rest Assured](https://github.com/rest-assured/rest-assured) for integration testing.

## How to Build and Run the Project

This project uses the [Gradle](https://gradle.org) build system, you can build the project locally just by typing the
following in the console:

```
./gradlew build
```

On Windows use `gradlew.bat` instead of `./gradlew`.

The output of the Gradle build is located in the  `build/` directory.

To run the project deploy the war-File in the `build/libs` directory to the application server of your choice.
Alternatively, you can use the Gradle Jetty plugin:

```
./gradlew jettyRun
```

### Swagger

Browse to the application root for API documentation:

[http://localhost:8080/](http://localhost:8080)

All REST endpoints can be tested locally with the Swagger UI frontend.

## How to Extend the Project

If you want to import the project in an IDE such as Eclipse or IntelliJ IDEA then Gradle provides a way to generate all
the necessary project files.

Generate Eclipse project:
```
./gradlew cleanEclipse eclipse
```

Generate IntelliJ IDEA project:
```
./gradlew cleanIdea idea
```

Alternatively, with IntelliJ IDEA you can also import the project from the Gradle model,
just follow [this guide](https://www.jetbrains.com/help/idea/2016.1/importing-project-from-gradle-model.html).

## Tasks

1. Search for users by last name:
    * Extend the existing API of the `UserService` to expose the new functionality via a new HTTP query parameter
      `lastName`, which will filter the users, by their last name.
        * (Optional) implement a prefix matching, thus if `lastName=bon` then we should get the user `Bond` as well.
        * (Optional) write a DAO integration test, service mocked test and a service REST integration test.

2. Insert new users into the system via the API
    * Implement an appropriate extension of the `UserService` with suitable REST endpoint
    * Persist new data
    * Validate the input data:
        * all user fields should be mandatory
        * Use an appropriate HTTP status code if the input is invalid.
        * (Optional) Return HTTP status code `Conflict` if the user email already exists.
        * (Optional) Check if the provided email has a valid syntax by using a simple regular expression.
    * (Optional) write a DAO integration test, service mocked test and a service REST integration test.

3. Implement a new JPA domain class `Exercise` and that represents an exercise done by the user.
    * An object of this class should hold all necessary relational information about a exercise and:
        * type (`RUNNING`, `CYCLING`, `SWIMMING`, `ROWING`, `WALKING`, `CIRCUIT_TRAINING`, `STRENGTH_TRAINING`,
          `FITNESS_COURSE`, `SPORTS`, `OTHER`)
        * start timestamp
        * duration in seconds
        * calories burned
        * distance in meters (optional attribute).
    * Don't forget to annotate your class with `@Entity` and to update the `persistence.xml` file.
    * Implement a new DAO to access entities of this `Exercise` class
    * Build a REST service to implement the following business logic:
        * list all existing exercises for a given user id.
            * add the ability to filter them by type.
            * add the ability to filter them by date.
        * get one specific exercise by providing the user and exercise id
        * create a new exercise by giving the user reference, which should exist in the system, and the necessary
          exercise data.
        * create some test exercises in the `TestData` class.
        * (Optional) Validate the input. If a user is not preset in the Database respond with HTTP status code
          `Not Found`, if some exercise attributes are missing return HTTP status code `Bad Request` and an appropriate
          error message.
        * (Optional) More validation:
          during exercise creation there should be checked that there is no other exercise already present,
          in the period where the new exercise will take place. If this is the case return an HTTP
          status code `Conflict` with appropriate error message.
    * (Optional) write a DAO integration test, service mocked test and a service REST integration test.

4. Implement a points and ranking system based on the user's exercises
    * Calculate the points a user receives for an exercise.
      A user gets one point per minute of the duration of the exercise plus the burnt calories.
    * Provide a REST endpoint to retrieve the accumulated points of a user for the last 4 weeks.
    * (Optional) Implement a caching mechanism for the calculated points on the server side.
      The cache should be valid for 5 minutes.
    * (Optional) write appropriate tests.

5. Implement user achievements:
    * If a user has trained at least 4 days in one week (i.e. from Monday until Sunday)
      for at least 30 minutes a day, he receives the achievement `TRAINING_ADDICT`.
    * The user's achievements should be retrievable via a REST call.
    * (Optional) Implement an achievement of your choice.
    * (Optional) write appropriate tests.

## How to Submit Your Solution

Before submitting make sure your project builds successfully by executing the following command:
```
./gradlew clean build
```

Then zip the project, but before that make sure to remove all binary/compiled data by doing a clean:
```
./gradlew clean
```

Send your solution as zip file using the [this online form](https://script.google.com/a/macros/egym.de/s/AKfycbwVMmgwNOQ_iq1Vk3z-FyJU_eQdx8QvzVUx9wbaFEXFNxi0uYnc/exec).
In order to avoid any misunderstandings, please fill out the same email and name you used in your application.
