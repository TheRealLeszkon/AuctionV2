# AuctionRevamped — Backend

Spring Boot backend for AuctionRevamped, a real-time IPL auction platform. Handles game lifecycle management, player bidding, team tracking, and live WebSocket events.

Also check out the [Frontend](https://github.com/geniusjoelraj/AuctionV2Frontend) built for this API for Ideas on how you can make use of it

---

## Tech Stack

- **Java 17+** with **Spring Boot**
- **WebSocket** via STOMP over SockJS
- **REST API** for all game and player management
- JSON over HTTP with a consistent error envelope

## Database Schema

<img width="1271" height="721" alt="AuctionV2FinalTableStructure drawio" src="https://github.com/user-attachments/assets/c81a37f6-00d7-4567-bd41-69c2dbe5826f" />

---

## Getting Started

### Prerequisites

- Java 17+
- Maven wrapper included (`./mvnw`)
- PostgreSQL DB

### Run Locally

```bash
# Clone the repository
git clone https://github.com/your-username/your-repo-name.git
cd your-repo-name

# Verify prerequisites
java -version
mvn -version

# Build the project
./mvnw clean install

# Run the Spring Boot application
./mvnw spring-boot:run

# (Optional) Package and run the JAR
./mvnw clean package
java -jar target/app-name.jar
```

Server starts on `http://localhost:6769`. No context path is configured — all paths are relative to the root.

### Environment

A `.env` file is used for configuration. Refer to `.env` for required variables.

```
DB_URL=jdbc:postgresql://localhost:5432/kevin
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

---

## API Overview

### Error Responses

All errors follow a consistent envelope:

```json
{
  "status": 400,
  "message": "Human readable error message"
}
```

| HTTP Status       | Triggered by               |
| ----------------- | -------------------------- |
| `400 Bad Request` | `IllegalArgumentException` |
| `409 Conflict`    | `IllegalStateException`    |

---

## Auth

### `POST /auth`

Validates credentials for a given game. The `username` field accepts `"host"`, `"admin"`, or an IPL team association (e.g. `"CSK"`).

**Request body:**

```json
{
  "gameId": 1,
  "username": "CSK",
  "password": "teampassword"
}
```

**Response `200 OK`:**

```json
{
  "message": "CSK Login Successful!"
}
```

**Error `400`** if password is incorrect or association is invalid.

---

## Players

| Method | Path              | Description         |
| ------ | ----------------- | ------------------- |
| `GET`  | `/players`        | Get all players     |
| `POST` | `/players`        | Create a player     |
| `POST` | `/players/bulk`   | Bulk create players |
| `GET`  | `/players/{type}` | Get players by type |

Player types: `WICKET_KEEPER`, `BATSMAN`, `BOWLER`, `ALL_ROUNDER`

### `POST /players` — Create a Player

**Request body:**

```json
{
  "name": "Virat Kohli",
  "imageLink": "https://example.com/virat.png",
  "type": "BATSMAN",
  "isForeign": false,
  "isUncapped": false,
  "isLegend": true,
  "country": "India",
  "batsmanStats": {
    "runs": 7263,
    "matches": 237,
    "battingAvg": 52.73,
    "strikeRate": 131.6
  },
  "bowlerStats": null,
  "allRounderStats": null
}
```

**Response `201 Created`:** Returns the saved `PlayerDTO` with a generated `id`.

### `GET /players/{type}` — Get Players by Type

**Example:** `GET /players/BATSMAN`

**Response `200 OK`:** `PlayerDTO[]`

**Error `400`** if type is invalid. Returns a descriptive map:

```json
{
  "error": "Invalid player type in URL!",
  "provided": "INVALID",
  "allowed": ["BATSMAN", "BOWLER", "WICKET_KEEPER", "ALL_ROUNDER"]
}
```

---

## Sets

Sets are groupings of players used in a game. Each entry carries metadata (base price, auction order, and points).

| Method | Path             | Description               |
| ------ | ---------------- | ------------------------- |
| `POST` | `/set`           | Create a set              |
| `GET`  | `/set`           | List all sets             |
| `POST` | `/set/{id}`      | Add a player to a set     |
| `POST` | `/set/{id}/bulk` | Bulk add players to a set |
| `GET`  | `/set/{id}`      | Get all players in a set  |

### `POST /set` — Create a Set

**Request body:**

```json
{ "name": "IPL 2026 Set" }
```

**Response `201 Created`:**

```json
{ "setId": 1, "name": "IPL 2026 Set" }
```

### `POST /set/{id}` — Add a Player to a Set

The `setId` in the path overrides any value in the body.

**Request body:**

```json
{
  "playerId": 42,
  "points": 120,
  "price": 500000,
  "order": 1
}
```

**Response `200 OK`:** Returns the saved `SetPlayerDTO` (with `setId` populated from path).

### `POST /set/{id}/bulk` — Bulk Add Players

**Request body:** Array of `SetPlayerDTO` objects. The `setId` field in each object is overridden by the path.

```json
[
  { "playerId": 42, "points": 120, "price": 500000, "order": 1 },
  { "playerId": 43, "points": 95, "price": 200000, "order": 2 }
]
```

---

## Games

The core of the API. A game ties together a set of players, 10 IPL teams, and auction rules.

### Game Lifecycle

```
INACTIVE → (start) → ACTIVE → (finalize) → FINALIZED → (end) → ENDED
                         ↑                        |
                         └──────── (resume) ──────┘
```

| Method | Path                         | Description                                           |
| ------ | ---------------------------- | ----------------------------------------------------- |
| `GET`  | `/game`                      | List all games                                        |
| `POST` | `/game`                      | Create a game (`status` forced to `INACTIVE`)         |
| `GET`  | `/game/{id}`                 | Get game details (includes team passwords)            |
| `GET`  | `/game/{id}/preview`         | Preview all players in the game's set                 |
| `POST` | `/game/{id}/start`           | Activate the game, create 10 teams                    |
| `POST` | `/game/{id}/finalize`        | Finalize auction, lock bids                           |
| `POST` | `/game/{id}/resume`          | Un-finalize, resume auction                           |
| `POST` | `/game/{id}/end`             | End game and compute rankings                         |
| `GET`  | `/game/{id}/results`         | Get final rankings (game must be `ENDED`)             |
| `POST` | `/game/{id}/results/publish` | Broadcast results to all team WebSocket subscriptions |

### `POST /game` — Create a Game

**Request body:**

```json
{
  "name": "Auction 2026",
  "setId": 1,
  "initialBalance": 1000000,
  "adminPassword": "admin123",
  "hostPassword": "host123",
  "playersPerTeam": 12,
  "batsmenPerTeam": 4,
  "bowlersPerTeam": 4,
  "allRounderPerTeam": 2,
  "wicketKeeperPerTeam": 2,
  "substitutesPerTeam": 2,
  "foreignPlayersPerTeam": 4,
  "maxForeignAllowed": 4,
  "unCappedPerTeam": 0,
  "legendsPerTeam": 0,
  "specialPlayersPerTeam": 0
}
```

All slot limit fields are optional and fall back to server defaults if omitted. `status` is always forced to `INACTIVE`.

**Response `201 Created`:** Returns `GameDTO`.

### `POST /game/{id}/start` — Start a Game

**Request body:**

```json
{ "command": "START" }
```

**Response `201 Created`:**

```json
{
  "message": "The game is now active! Team view will now be available.",
  "command": "START",
  "gameStatus": "ACTIVE"
}
```

### `POST /game/{id}/finalize` — Finalize a Game

No request body required.

**Response `200 OK`:**

```json
{
  "message": "Game Finalized and Auction Has Ended! Allow Players to Select their teams!",
  "command": "",
  "gameStatus": "FINALIZED"
}
```

### `POST /game/{id}/resume` — Resume a Finalized Game

No request body required.

**Response `200 OK`:**

```json
{
  "message": "Game Resumed!",
  "command": "",
  "gameStatus": "ACTIVE"
}
```

---

### Teams

| Method | Path                                      | Description                       |
| ------ | ----------------------------------------- | --------------------------------- |
| `GET`  | `/game/{id}/team`                         | All teams in the game             |
| `GET`  | `/game/{id}/team/{association}`           | Specific team (`CSK`, `MI`, etc.) |
| `GET`  | `/game/{id}/team/{association}/purchases` | Players purchased by a team       |

> All team endpoints require the game to be `ACTIVE` or later; returns `400` if `INACTIVE`.

**Example `GET /game/1/team/CSK` response:**

```json
{
  "id": 5,
  "gameId": 1,
  "name": "Chennai",
  "association": "CSK",
  "balance": 850000,
  "points": 220,
  "qualified": true,
  "playerCount": 12,
  "batsmanCount": 4,
  "bowlerCount": 4,
  "allRounderCount": 2,
  "wicketKeeperCount": 2,
  "uncappedCount": 0,
  "legendCount": 0,
  "specialCount": 0,
  "foreignCount": 4
}
```

---

### Players in Game

| Method | Path                              | Description                     |
| ------ | --------------------------------- | ------------------------------- |
| `GET`  | `/game/{id}/players`              | All players with auction status |
| `GET`  | `/game/{id}/players/{playerType}` | Filtered by type                |
| `GET`  | `/game/{id}/players/unsold`       | Only unsold players             |
| `GET`  | `/game/{id}/players/sold`         | Only sold players               |
| `GET`  | `/game/{id}/player/{name}`        | Lookup by player name           |

All endpoints return `CompletePlayer[]` (or a single `CompletePlayer` for the name lookup). Requires game to be `ACTIVE`.

**`CompletePlayer` shape:**

```json
{
  "id": 1,
  "name": "Virat Kohli",
  "imageLink": "https://example.com/virat.png",
  "type": "BATSMAN",
  "isUncapped": false,
  "isLegend": true,
  "isForeign": false,
  "country": "India",
  "batsmanStats": {
    "runs": 7263,
    "matches": 237,
    "battingAvg": 52.73,
    "strikeRate": 131.6
  },
  "bowlerStats": null,
  "allRounderStats": null,
  "setId": 1,
  "price": 2000000,
  "points": 220,
  "order": 3,
  "status": "UNSOLD"
}
```

---

### Auction Actions

| Method | Path                  | Description                     |
| ------ | --------------------- | ------------------------------- |
| `POST` | `/game/{id}/purchase` | Mark a player as sold to a team |
| `POST` | `/game/{id}/refund`   | Refund a purchased player       |
| `GET`  | `/game/{id}/audit`    | Game audit log                  |

### `POST /game/{id}/purchase` — Purchase a Player

Validates slot constraints and balance. Broadcasts purchase, team update, and audit events over WebSocket.

**Request body:**

```json
{
  "playerId": 42,
  "teamAssociation": "CSK",
  "finalBid": 750000
}
```

**Response `200 OK`:**

```json
{
  "playerStatus": "SOLD",
  "soldTo": "CSK",
  "soldFor": 750000
}
```

**Common errors:**

- `400` — Bid amount is zero or negative
- `400` — Game not `ACTIVE`
- `409` — Player already sold
- `409` — Bid below base price

### `POST /game/{id}/refund` — Refund a Player

Reverses a purchase: restores team balance, points, and slot counts. Broadcasts refund, team update, and audit events over WebSocket.

**Request body:**

```json
{ "playerId": 42 }
```

**Response `200 OK`:**

```json
{
  "playerName": "Virat Kohli",
  "playerStatus": "UNSOLD",
  "message": "Refund was successful!"
}
```

### `GET /game/{id}/audit` — Audit Log

Returns all purchase and refund transactions for a game in reverse chronological order.

**Response `200 OK`:** `GameLog[]`

---

### Substitute Selection

After finalization, each team must remove substitutes to lock in their final roster.

| Method | Path                                     | Description                            |
| ------ | ---------------------------------------- | -------------------------------------- |
| `POST` | `/game/{id}/selection`                   | Submit substitute removals for a team  |
| `GET`  | `/game/{id}/selection/locked-in`         | Check how many teams have locked in    |
| `GET`  | `/game/{id}/selection/{teamAssociation}` | Get a team's final locked-in selection |

### `POST /game/{id}/selection` — Submit Substitute Removal

Game must be `FINALIZED`. Team must not have already locked in.

**Request body:**

```json
{
  "teamAssociation": "CSK",
  "substitutes": [101, 102]
}
```

The `substitutes` array contains player IDs to be marked as `SUBSTITUTED`. If the team is already at or below the roster limit, send an empty array.

**Response `200 OK`:**

```json
{
  "message": "Current Selection Locked in. Best of Luck!",
  "command": "",
  "gameStatus": "FINALIZED"
}
```

### `GET /game/{id}/selection/locked-in`

Requires game to be `FINALIZED` or `ENDED`.

**Response `200 OK`:**

```json
{
  "lockedInCount": 4,
  "lockedInTeams": ["CSK", "MI", "RCB", "KKR"]
}
```

---

## WebSocket

Real-time events are broadcast over STOMP. Connect via SockJS or a raw WebSocket at `/ws`.

- **App destination prefix:** `/app`
- **Broker prefix:** `/topic`

### Connecting (JavaScript / SockJS example)

```javascript
const socket = new SockJS("http://localhost:8080/ws");
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  // Subscribe to bid updates for game 1
  stompClient.subscribe("/topic/game/1/bids", (frame) => {
    const event = JSON.parse(frame.body);
    console.log(event.eventType, event.payload);
  });
});
```

### Event Envelope

All WebSocket messages use this wrapper:

```json
{
  "eventType": "BID",
  "timestamp": "2026-02-22T12:34:56Z",
  "payload": { ... }
}
```

---

### Sending Messages (Client → Server)

| Destination               | Payload      | Description                                         |
| ------------------------- | ------------ | --------------------------------------------------- |
| `/app/game/{gameId}/bids` | `BidRequest` | Broadcast the current bid amount to all subscribers |

**`BidRequest` body:**

```json
{ "currentBid": 750000 }
```

> If the game is not `ACTIVE`, an `ERROR` event is broadcast back to the bids topic instead.

---

### Subscriptions (Server → Client)

| Topic                                          | Payload Type         | `eventType`      | Triggered by                      |
| ---------------------------------------------- | -------------------- | ---------------- | --------------------------------- |
| `/topic/game/{gameId}/bids`                    | `BidRequest`         | `BID` or `ERROR` | Client sends bid via `/app/...`   |
| `/topic/game/{gameId}/purchases/{association}` | `PurchasedPlayer`    | `PURCHASE`       | `POST /game/{id}/purchase`        |
| `/topic/game/{gameId}/refunds/{association}`   | `RefundConfirmation` | `REFUND`         | `POST /game/{id}/refund`          |
| `/topic/game/{gameId}/team/{association}`      | `TeamDTO`            | `TEAM_UPDATE`    | Purchase or refund                |
| `/topic/game/{gameId}/audit`                   | `GameLog`            | `AUDIT`          | Purchase or refund                |
| `/game/{gameId}/results/{association}`         | `Ranking`            | `RESULT`         | `POST /game/{id}/results/publish` |

> ⚠️ **Note:** The results topic (`/game/{gameId}/results/{association}`) does **not** use the `/topic/` prefix. This is a known inconsistency in the current implementation.

**`PurchasedPlayer` payload example:**

```json
{
  "playerId": 42,
  "name": "Virat Kohli",
  "playerType": "BATSMAN",
  "boughtFor": 750000,
  "points": 220,
  "isForeign": false,
  "isLegend": true,
  "isUncapped": false
}
```

**`GameLog` (audit) payload example:**

```json
{
  "playerName": "Virat Kohli",
  "teamName": "Chennai",
  "amount": 750000,
  "type": "PURCHASE",
  "playerStatus": "SOLD"
}
```

---

## Enums Reference

| Enum              | Values                                                            |
| ----------------- | ----------------------------------------------------------------- |
| `GameStatus`      | `INACTIVE`, `ACTIVE`, `FINALIZED`, `ENDED`                        |
| `GameCommand`     | `START`                                                           |
| `PlayerType`      | `WICKET_KEEPER`, `BATSMAN`, `BOWLER`, `ALL_ROUNDER`               |
| `PlayerStatus`    | `SOLD`, `UNSOLD`, `SUBSTITUTED`                                   |
| `IPLAssociation`  | `CSK`, `DC`, `GT`, `KKR`, `LSG`, `MI`, `PBKS`, `RR`, `RCB`, `SRH` |
| `TransactionType` | `PURCHASE`, `REFUND`                                              |

---

## Known Issues

- The results WebSocket topic (`/game/{gameId}/results/{association}`) is missing the `/topic/` prefix compared to all other broadcast destinations. Clients must subscribe to the exact path without `/topic/`.
