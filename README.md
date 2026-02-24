# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](sequence_diagram.svg)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowGKEpujKcplu8SqYCqwYam62q6vq+lGBAahoAA5MwVpojeAUOkmvrOl2MA9n227uRZ-rMtMl7QEgABeKAcFGMZxoUWlFfAyCpjA6YAIwETmqh5vM0FFiW9SpWgEDMAAZr4nANvR+W8oFFlWeFW5uYKbn1CA8QoCAapNEZ6kYmpOxrNt8guFdAKknF6obrAD3ruKIDQCi4CVSBJX1PNJoGH93Wga6dV6g1zWtdGKCxgp6HwsmvXYf1ThDaMYwjWNBZjJN0DTZD8TQy1K1NrtYMAzAFipKDVK3vy3lgM+8Tnpe17DoVlTLiF4pPhz2iyvK+lrQuApU529Ss6+JXtjppYOeKGSqABmAK-9ElgYR5bEfUgxLAbsGXlR9YkTABsWw2XUoyUYA4XhBH6ShltG+RJuIWb+uG+TDGeN4fj+F4KDoDEcSJMHocOb4WCiXt1SSdIEb8RG7QRt0PRyaoCnDBRpudQn8KVAr9R557hQa5tLrWUJMf2bXp5OWoLn0zIjP0jAjIs5e7Mvto-nrdzQr1KFAt9-IwvGh7SGxaqGqs9DeQFDABNQGL+6a5Z1flf2lPVaWtUUaTsPtYjNuVKJaYY8N-K4xNxaEwqxPH7768bcVUsBoL8it1zHccCgbgx4e4y37m-IevMMgzAgDQL+49gCT1AT-P+Esq6lVpmIPexczL+mjo3f8CBAI4ORh-GoLxNKF0wqje2MBcL4VGHRJs-tmL+BROufw2BxQan4miGAABxJUGg4773qA0PhqcM72CVLnae6AkbaWIaXWRFdiHx0-sgHIAi5j2TRFotQTcSS-3bqOTuTJWa9yvGAlB95gqjzgZYiekUy4z2evPeqUAmpLzQCvB+a9rGSy3qVHeoMRGSmfh4mGbV4YdXkd1S+6NMbZlvvme+U0n5HwiWTRhDF-FoOlt-YARiCodw0WAPRqgMQD3FjYke-MYBUBNEgdcUicymFyaQqycAIB9mOh1AAPHonk5QQnYNhLg3RgiVZq0rqE8YLS1AoQaHMpUABJaQKEBrhGCIEEEmx4i6hQG6TkLsfYgmSKANURzILm1dt8eZAA5a53srYXE6BQrWVC7YO3ocsnMizfkoDWRsrZOzlh7IOVcvWVsrZnIQBcyF8wbmnOWA8p50KDavOyYxAOAQOAAHY3BOBQE4GIEZghwC4gANngBOQweiYBFGoWoshjRWgdEkdI4m+csyormO8xMoyvxKPguXHlSpHl8vVqovJMBDxyBQHojEcBaV6IMS3SmDNikmK7uYiinNjE1L5hKJBCCnHKNnvFexi8Oo+JLOAxcMrglYI7Cyw+7imotSiQjeM58epfISTfXMKTCy+KJhkj1uwsXtJdVZE1RTB4dzleiRV8z9VasNY+fhSoPTRv2lmuYstOzy0UTSo8CqlRTMIVKsZJCXW6TGPMoF5tNnbP5RhOJ1DvkEUbes5tIKsXMMDpYQBNlNhhyQAkMAw6+wQDHQAKQgOKfNhh-DnJOoyu2zLJJNGZDJHo8yZEiqQlmbAcLh1QC6TZKAawe1tqLg8GtwrKKitGKe4A57L3QBvas6Qpka3MqsgAK0XWgRVzJsBaGTRWlAhJm6uTlh5BN2qzE9z1VYg1QVanGoKZPZxcjXFWoiV4210B7WoI6dvXsu8EOhLdVDTJJ9oln0oR2-1g1A2jWDfjUN6T3Uw1frm6mcaNVty1czFNP6qkb0w-wpky6Iryh7Ral6AAhEMMA1Vkc3lZJ1NHSH+jUxwNVXqYm+viemH52NknjRDWkvQ64USwZyAJjDMrBk7QQxR0qh1jpqnaMGGYn6oCXTPZQILT054BirOaGAYYkIkbXs6vNumi0BP9IGM0lg4uRjht6guHzWN9XTJmLGON8zm242kkA7BZrMGA+Kb9cxKyZZpuGZCWKkvUwwfG8W00IPyokwW9D6aZPgcg3S7NQtIqpq3IJz+7mf7OpLpKfrUG5iVqIf+tLYE72fL6nQrMA6mKBy8O+8dk7TvykQMGWAwBsCnsIMRjd5gt2lgaEnFOacM7GFiYKuEB1uB4E05XLzAObtKsB1ANV8HUuaqQ2DvA5TKlaZk9IQBTIJtDYnpB3Q+hlSReu3gBLKOZVAwQCDTrWt6iE6gCZ5jBXbZ9QAKyO1K9Zmiq8Dq+AQBwLKzAQCQ6MNoPQBgXMjZld1kTKCEdQDdBUqTgUebBTR0Awwt0EE45F-jy1NPidzcCYDYGmC9N1tLDTunPqWOM7RizyzZXEWpMfl9XsvPsqysF5rvHYukPaerpLzzon4fu5u6zeXKOlf1BVxj+xMpPcgwI7r1eJPQcwDJxTk34Meo3Yt-lxM1uaG2447jCrnPZXc9d-zj3wuvdRtcyn-3qWPwlppxt6tX5N66V24VtGB2GGNgYkAA)

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowGKEpujKcplu8SqYCqwYam62q6vq+lGBAahoAA5MwVpojeAUOkmvrOl2MA9n227uRZ-rMtMl7QEgABeKAcFGMZxoUWlFfAyCpjA6YAIwETmqh5vM0FFiW9SpWgEDMAAZr4nANvR+W8oFFlWeFW5uYKbn1CA8QoCAapNEZ6kYmpOxrNt8guFdAKknF6obrAD3ruKIDQCi4CVSBJX1PNJoGH93Wga6dV6g1zWtdGKCxgp6HwsmvXYf1ThDaMYwjWNBZjJN0DTZD8TQy1K1NrtYMAzAFipKDVK3vy3lgM+8Tnpe17DoVlTLiF4pPhz2iyvK+lrQuApU529Ss6+JXtjppYOeKGSqABmAK-9ElgYR5bEfUgxLAbsGXlR9YkTABsWw2XUoyUYA4XhBH6ShltG+RJuIWb+uG+TDGeN4fj+F4KDoDEcSJMHocOb4WCiXt1SSdIEb8RG7QRt0PRyaoCnDBRpudQn8KVAr9R557hQa5tLrWUJMf2bXp5OWoLn0zIjP0jAjIs5e7Mvto-nrdzQr1KFAt9-IwvGh7SGxaqGqs9DeQFDABNQGL+6a5Z1flf2lPVaWtUUaTsPtYjNuVKJaYY8N-K4xNxaEwqxPH7768bcVUsBoL8it1zHccCgbgx4e4y37m-IevMMgzAgDQL+49gCT1AT-P+Esq6lVpmIPexczL+mjo3f8CBAI4ORh-GoLxNKF0wqje2MBcL4VGHRJs-tmL+BROufw2BxQan4miGAABxJUGg4773qA0PhqcM72CVLnae6AkbaWIaXWRFdiHx0-sgHIAi5j2TRFotQTcSS-3bqOTuTJWa9yvGAlB95gqjzgZYiekUy4z2evPeqUAmpLzQCvB+a9rGSy3qVHeoMRGSmfh4mGbV4YdXkd1S+6NMbZlvvme+U0n5HwiWTRhDF-FoOlt-YARiCodw0WAPRqgMQD3FjYke-MYBUBNEgdcUicymFyaQqycAIB9mOh1AAPHonk5QQnYNhLg3RgiVZq0rqE8YLS1AoQaHMpUABJaQKEBrhGCIEEEmx4i6hQG6TkLsfYgmSKANURzILm1dt8eZAA5a53srYXE6BQrWVC7YO3ocsnMizfkoDWRsrZOzlh7IOVcvWVsrZnIQBcyF8wbmnOWA8p50KDavOyYxAOAQOAAHY3BOBQE4GIEZghwC4gANngBOQweiYBFGoWoshjRWgdEkdI4m+csyormO8xMoyvxKPguXHlSpHl8vVqovJMBDxyBQHojEcBaV6IMS3SmDNikmK7uYiinNjE1L5hKJBCCnHKNnvFexi8Oo+JLOAxcMrglYI7Cyw+7imotSiQjeM58epfISTfXMKTCy+KJhkj1uwsXtJdVZE1RTB4dzleiRV8z9VasNY+fhSoPTRv2lmuYstOzy0UTSo8CqlRTMIVKsZJCXW6TGPMoF5tNnbP5RhOJ1DvkEUbes5tIKsXMMDpYQBNlNhhyQAkMAw6+wQDHQAKQgOKfNhh-DnJOoyu2zLJJNGZDJHo8yZEiqQlmbAcLh1QC6TZKAawe1tqLg8GtwrKKitGKe4A57L3QBvas6Qpka3MqsgAK0XWgRVzJsBaGTRWlAhJm6uTlh5BN2qzE9z1VYg1QVanGoKZPZxcjXFWoiV4210B7WoI6dvXsu8EOhLdVDTJJ9oln0oR2-1g1A2jWDfjUN6T3Uw1frm6mcaNVty1czFNP6qkb0w-wpky6Iryh7Ral6AAhEMMA1Vkc3lZJ1NHSH+jUxwNVXqYm+viemH52NknjRDWkvQ64USwZyAJjDMrBk7QQxR0qh1jpqnaMGGYn6oCXTPZQILT054BirOaGAYYkIkbXs6vNumi0BP9IGM0lg4uRjht6guHzWN9XTJmLGON8zm242kkA7BZrMGA+Kb9cxKyZZpuGZCWKkvUwwfG8W00IPyokwW9D6aZPgcg3S7NQtIqpq3IJz+7mf7OpLpKfrUG5iVqIf+tLYE72fL6nQrMA6mKBy8O+8dk7TvykQMGWAwBsCnsIMRjd5gt2lgaEnFOacM7GFiYKuEB1uB4E05XLzAObtKsB1ANV8HUuaqQ2DvA5TKlaZk9IQBTIJtDYnpB3Q+hlSReu3gBLKOZVAwQCDTrWt6iE6gCZ5jBXbZ9QAKyO1K9Zmiq8Dq+AQBwLKzAQCQ6MNoPQBgXMjZld1kTKCEdQDdBUqTgUebBTR0Awwt0EE45F-jy1NPidzcCYDYGmC9N1tLDTunPqWOM7RizyzZXEWpMfl9XsvPsqysF5rvHYukPaerpLzzon4fu5u6zeXKOlf1BVxj+xMpPcgwI7r1eJPQcwDJxTk34Meo3Yt-lxM1uaG2447jCrnPZXc9d-zj3wuvdRtcyn-3qWPwlppxt6tX5N66V24VtGB2GGNgYkAA

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
