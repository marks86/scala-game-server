
## Simple RESTful game server app based on Akka HTTP and Spray JSON

The game should implement: [scala-game-card-core](https://github.com/marks86/scala-game-card-core)

### Prerequisites
* JDK 1.8
* Scala 2.12.8
* sbt 1.2.8

### Running

```
sbt run
```

### Playing

The app already includes [scala-game-card-blackjack](https://github.com/marks86/scala-game-card-blackjack).
Since request validation isn't implemented in the game yet, only valid action sequence will work.
Supported actions: DEAL, HIT, STAND.

*Deal request example:*
```javascript
curl -H "Content-Type: application/json" -X GET -d '{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 1}' http://localhost:8080/game
```

*Deal response:*
```javascript
{
  "bet": 1.0,
  "dealer": {
    "hand": [
      {
        "rank": "FIVE",
        "suit": "HEARTS"
      },
      {
        "rank": "NINE",
        "suit": "DIAMONDS"
      }
    ],
    "hasBJ": false,
    "value": 14
  },
  "player": {
    "hand": [
      {
        "rank": "ACE",
        "suit": "SPADES"
      },
      {
        "rank": "KING",
        "suit": "SPADES"
      }
    ],
    "hasBJ": true,
    "value": 21
  },
  "roundEnded": true,
  "outcome": "PLAYER",
  "totalWin": 1.5
}
```

### Further work

* Add stakes
* Add authentication
* User balance
