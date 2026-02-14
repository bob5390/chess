# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowGKEpujKcplu8SqYCqwYam6MBqTsMDiiA0AouAN4BQ6Sa+s6XYwD2fbbu5Fn+sy0yXtASAAF4oBwUYxnGhRaXl8DIKmMDpgAjAROaqHm8zQUWJb1D4VV6jV9W7HRTZxeqG6wDqepRUaHAQGoaAAOTMFaaLZbygUWVZxX9m5IHVP6AByEBgCKvicE1KCxgp6HwsmnXYTAACseH9fyQ0FmMo3QONRGGGgd0wAAZo9s2Ngxw65SdLpLa+BWCsj9IwIyYDPvE56Xte2MCkF9ShU+xPaLK8r6YdC5k6jhUExjnbtjppYOeKGSqABmCc5dElgYR5bEfUgxLJLsGXlR9YkTAkuKw2bWfSUYA4f9oyi9F4vK8rMvwYh8sS1LDb0Yx3h+P4XgoOgMRxIktv2w5vhYKJgqgfUDTSBG-ERu0EbdD0cmqApwwUXLrVXfClSc-UkfG4UgvM-UNn2G79lCW7TlqC5pVY7e-Lefjl5Ey+2j+UduWVMuIXilTFfyLTxqy0nDP7kLllo6zW4XR51c4xwKDcMeZe9-IVeM-ewUZDMEA0AG1PN5FE-ALFqoagT015AUMCg1AHfHflnbdr252Y+1XuSpN8TTQ1B-vZhX0a91Th9drA1AyNxZgwqt-3wRhbBaCUtS3WYA5Rk64ACShRSZdysjDE0BgC5XxjvUcBD0JqNWjC9FqT92qiXqH9fCn9Ab5h-mNKKRVobpygalZCc0kZF0XKnGAFhUioI-GZf0rtTy835incqYFNIx2furTWpCRhMMtsxfwKJ1z+GwOKDU-E0QwAAOJKg0B7YR3sNEB2DvYJUEc25IQIXHHhpZE7mJTifbuhVkA5C0XMeyaIXFqFziSVBVIWGjmsu47R5cryVyPjXIUFMG4wCoCaJA65jE5hbhRD0ICb4UUAfvX+h94FoNPkVc+XD7EVQAVAOqD9cGvXjKrSoRC34f2zOQ+YCsQZZPGiUspQCmw5LYXACAfYUDgAUgAHjgPEfkHjyiFIeLCXhgScwCIQIBKxXddJjASWoFCDRxjrOgdIFCPVwjBECCCTY8RdQoDdJyFCStpbLGSKANUlzIIKxud8dZ11nmm2VhcToojhbiK6rhKRaztGbO2UqXZ+zDnHOWKc85Ty9avJBPckAjyIbXLNiCd5nz9aSx+TIzwVsAgcAAOxuCcCgJwMQIzBDgFxAAbPACchgPEwCKC-T26DGitA6EYkxt8o5ZmxXMP5iZLEzOsWY9AQqlQfJFQLZZbDDxyBQB4jEcBmUeK8fnfuvico42VeiNV6ySZ+JnvUR8milQpM3mk6qpSZqZJLGE1h9jToFP7nou1U0HXlOam9apHUJF1IBrmfMzSD5tPSb6zpDFUnSD8MgEAKqrVzBgLsl1TM3VozOlM6+gYzSWDDEhZ6lTo7-MIS-YhWsGlhqaZQv+6ztS6n1HqbIqV1wmkLTAfaORVDmy6Wa3JDj6geLZg4jmVj6gaqPKqpUCylkSpWSLHZeyFYHKOaKjClbg1AoIquqFm7EayOtpYEeNlNgOyQAkMAZ6+wQEvQAKQgOKVNhh-AorVOy9WnLhbeyaMyGSPR1mmKNkhLM2AEDADPVAXpNkoBrFXVu2O0yvwJylYpUYkHoOUDg9ARDELpCmSXWwgAVi+tAarmTYC0Ea+dKBCR51cpfPVg9-F+Fo3O1xJrQk5NrsFajnG30RXlDx+QG94rervjGp10BM0IJzR6y+XrKrRo6aW-Bgbam9VDYNcNDbSwTTUzNAdcbbUFuDOaGAxb7aP26dmwqubPVFNLBZ6sNm0AaYDWIndXV0yZjIXWmikaYDJrQFDZg5HxQEbTV2yzNZwyMOPfZjsI7hN92U+KtDkoaMqq1f+RZCqSNeukQQtWgKa0EqYtbLw0Gr03tq-KRAwZYDAGwJBwgu80Bst0S572vt-aB2DsYCxqG4T1BANwPA2qitfl-WlybLX1VTagNq5j7MB6MwmytjEU9O7kxkCPJkhhaO6H0MqW1zW8CyeyUOthTnMupf9FdqAXmqk+fK99EhumgYRtaaF3wCAOA7WYIt67p29AGFM-J4dVkOFiGc2N57K2F2zfMiV5DALvp7tGDIoAA

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
