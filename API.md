# AuctionRevamped API Reference

Full REST and WebSocket API exposed by the AuctionRevamped backend.

**Base URL:** `http://localhost:8080` (no context path configured)

---

## Error Format

```json
{
  "status": 400,
  "message": "Human readable error message"
}
```

| HTTP Status | Triggered by |
|---|---|
| `400 Bad Request` | `IllegalArgumentException` |
| `409 Conflict` | `IllegalStateException` |

---

## Enums

| Enum | Values |
|---|---|
| `GameStatus` | `INACTIVE`, `ACTIVE`, `FINALIZED`, `ENDED` |
| `GameCommand` | `START` |
| `PlayerType` | `WICKET_KEEPER`, `BATSMAN`, `BOWLER`, `ALL_ROUNDER` |
| `PlayerStatus` | `SOLD`, `UNSOLD`, `SUBSTITUTED` |
| `TransactionType` | `PURCHASE`, `REFUND` |
| `IPLAssociation` | `CSK`, `DC`, `GT`, `KKR`, `LSG`, `MI`, `PBKS`, `RR`, `RCB`, `SRH` |

---

## Data Shapes

### `AuthRequest`
```json
{
  "gameId": 1,
  "username": "CSK",
  "password": "teampassword"
}
```
`username` accepts `"admin"`, `"host"`, or any `IPLAssociation` value.

---

### `GameDTO`
```json
{
  "id": 1,
  "setId": 10,
  "name": "Auction 2026",
  "status": "INACTIVE",
  "initialBalance": 1000000,
  "adminPassword": "admin123",
  "hostPassword": "host123",
  "playersPerTeam": 12,
  "batsmenPerTeam": 4,
  "bowlersPerTeam": 4,
  "allRounderPerTeam": 2,
  "wicketKeeperPerTeam": 2,
  "substitutesPerTeam": 2,
  "unCappedPerTeam": 0,
  "legendsPerTeam": 0,
  "specialPlayersPerTeam": 0,
  "foreignPlayersPerTeam": 4,
  "maxForeignAllowed": 4,
  "teamPasswords": [
    { "association": "CSK", "password": "cskpass" }
  ]
}
```
`teamPasswords` is only populated in `GET /game/{id}`. Slot limit fields default to server values if omitted in `POST /game`.

---

### `PlayerDTO`
```json
{
  "id": 1,
  "name": "Virat Kohli",
  "imageLink": "https://example.com/virat.png",
  "type": "BATSMAN",
  "isForeign": false,
  "isUncapped": false,
  "isLegend": true,
  "country": "India",
  "batsmanStats": { "runs": 7263, "matches": 237, "battingAvg": 52.73, "strikeRate": 131.6 },
  "bowlerStats": null,
  "allRounderStats": null
}
```

Stat sub-objects:
- `BatsmanStatsDTO`: `runs`, `matches`, `battingAvg`, `strikeRate`
- `BowlerStatsDTO`: `matches`, `wickets`, `economy`, `bestFigure`
- `AllRounderStatsDTO`: `runs`, `wickets`, `matches`, `strikeRate`

---

### `SetDTO`
```json
{ "setId": 1, "name": "IPL 2026 Set" }
```

### `SetPlayerDTO`
```json
{ "setId": 1, "playerId": 101, "points": 120, "price": 500000, "order": 1 }
```

---

### `TeamDTO`
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

### `CompletePlayer`
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
  "batsmanStats": { "runs": 7263, "matches": 237, "battingAvg": 52.73, "strikeRate": 131.6 },
  "bowlerStats": null,
  "allRounderStats": null,
  "setId": 1,
  "price": 2000000,
  "points": 220,
  "order": 3,
  "status": "UNSOLD"
}
```
`status` is null in the preview endpoint (game not yet started). Present and populated for all in-game player endpoints.

---

### `PurchasedPlayer`
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

### `PurchaseRequest`
```json
{ "playerId": 42, "teamAssociation": "CSK", "finalBid": 750000 }
```

### `PurchaseConfirmation`
```json
{ "playerStatus": "SOLD", "soldTo": "CSK", "soldFor": 750000 }
```

---

### `RefundRequest`
```json
{ "playerId": 42 }
```

### `RefundConfirmation`
```json
{ "playerName": "Virat Kohli", "playerStatus": "UNSOLD", "message": "Refund was successful!" }
```

---

### `GameControlMessage`
```json
{
  "message": "The game is now active! Team view will now be available.",
  "command": "START",
  "gameStatus": "ACTIVE"
}
```

---

### `SubstituteRemovalRequest`
```json
{ "teamAssociation": "CSK", "substitutes": [101, 102] }
```
`substitutes` is an array of player IDs to mark as `SUBSTITUTED`. Send an empty array if team is already at roster limit.

### `LockedInUpdate`
```json
{ "lockedInCount": 4, "lockedInTeams": ["CSK", "MI", "RCB", "KKR"] }
```

---

### `Ranking`
```json
{
  "place": 1,
  "teamStats": {
    "id": 5, "gameId": 1, "name": "Chennai", "association": "CSK",
    "balance": 850000, "points": 220, "qualified": true
  },
  "finalTeam": [
    { "name": "Virat Kohli", "boughtFor": 750000, "type": "BATSMAN" }
  ],
  "substitutes": [
    { "name": "Bench Player", "boughtFor": 200000, "type": "BOWLER" }
  ],
  "isQualified": true
}
```
Ranked by: qualified status → points → remaining balance.

---

### `BidRequest` (WebSocket)
```json
{ "currentBid": 750000 }
```

### `WebSocketEvent<T>` Envelope
```json
{
  "eventType": "BID",
  "timestamp": "2026-02-22T12:34:56Z",
  "payload": { ... }
}
```

---

## REST API

### Auth

#### `POST /auth`
- **Body:** `AuthRequest`
- **Response:** `{ "message": "CSK Login Successful!" }`
- **Error `400`:** Wrong password or invalid association.

---

### Players

#### `GET /players`
- **Response:** `PlayerDTO[]`

#### `POST /players`
- **Body:** `PlayerDTO`
- **Response `201`:** `PlayerDTO` (with generated `id`)

#### `POST /players/bulk`
- **Body:** `PlayerDTO[]`
- **Response `201`:** `PlayerDTO[]`

#### `GET /players/{type}`
- **Path param:** `BATSMAN`, `BOWLER`, `WICKET_KEEPER`, or `ALL_ROUNDER`
- **Response `200`:** `PlayerDTO[]`
- **Error `400`:** Returns `{ "error": "...", "provided": "INVALID", "allowed": [...] }`

---

### Sets

#### `POST /set`
- **Body:** `SetDTO`
- **Response `201`:** `SetDTO`

#### `GET /set`
- **Response:** `SetDTO[]`

#### `POST /set/{id}`
- **Body:** `SetPlayerDTO` — `setId` in body is overridden by path `{id}`
- **Response `200`:** `SetPlayerDTO`

#### `POST /set/{id}/bulk`
- **Body:** `SetPlayerDTO[]` — each `setId` overridden by path `{id}`
- **Response `200`:** `SetPlayerDTO[]`

#### `GET /set/{id}`
- **Response:** `SetPlayerDTO[]`
- **Error `400`:** If set does not exist.

---

### Games

#### `GET /game`
- **Response:** Raw `Game[]` entity list (all games, all statuses).

#### `POST /game`
- **Body:** `GameDTO` — `status` is forced to `INACTIVE`; slot limits optional (server defaults apply).
- **Response `201`:** `GameDTO`
- **Error `400`:** Name already exists, or `setId` doesn't exist.

#### `GET /game/{id}`
- **Response:** `GameDTO` (includes `teamPasswords` array)

#### `GET /game/{id}/preview`
- **Response:** `CompletePlayer[]` — all players in the game's set; `status` field will be `null` (game not yet started).

#### `POST /game/{id}/start`
- **Body:** `GameControlMessage` with `command: "START"`
- **Response `201`:** `GameControlMessage`
- **Error `409`:** Game already `ACTIVE` or not `INACTIVE`.

#### `GET /game/{id}/team`
- **Response:** `TeamDTO[]`
- **Error `400`:** Game is `INACTIVE`.

#### `GET /game/{id}/team/{association}`
- **Path param:** `association` — e.g., `CSK`, `MI` (case-insensitive)
- **Response:** `TeamDTO`
- **Error `400`:** Invalid association or game `INACTIVE`.

#### `GET /game/{id}/team/{association}/purchases`
- **Response:** `PurchasedPlayer[]`
- **Error `400`:** Invalid association or game `INACTIVE`.

#### `GET /game/{id}/players`
- **Response:** `CompletePlayer[]` (all players with auction `status`)
- **Error `400`:** Game `INACTIVE`.

#### `GET /game/{id}/players/{playerType}`
- **Path param:** `playerType` — e.g., `BATSMAN` (case-insensitive)
- **Response:** `CompletePlayer[]` filtered by type
- **Error `400`:** Invalid type or game `INACTIVE`.

#### `GET /game/{id}/players/unsold`
- **Response:** `CompletePlayer[]` where `status == UNSOLD`
- **Error `400`:** Game `INACTIVE`.

#### `GET /game/{id}/players/sold`
- **Response:** `CompletePlayer[]` where `status == SOLD`
- **Error `400`:** Game `INACTIVE`.

#### `GET /game/{id}/player/{name}`
- **Path param:** `name` — exact player name (URL-encoded if needed)
- **Response:** `CompletePlayer`

#### `POST /game/{id}/purchase`
- **Body:** `PurchaseRequest`
- **Response `200`:** `PurchaseConfirmation`
- **Side effect:** Broadcasts `PURCHASE`, `TEAM_UPDATE`, and `AUDIT` WebSocket events.
- **Errors:**
  - `400` — Game not `ACTIVE`, bid ≤ 0
  - `409` — Player already sold, bid below base price, slot constraint violated

#### `POST /game/{id}/refund`
- **Body:** `RefundRequest`
- **Response `200`:** `RefundConfirmation`
- **Side effect:** Broadcasts `REFUND`, `TEAM_UPDATE`, and `AUDIT` WebSocket events.
- **Errors:**
  - `400` — Game not `ACTIVE`
  - `409` — Player not `SOLD`, player not assigned to a team

#### `GET /game/{id}/audit`
- **Response:** `GameLog[]` (reverse chronological)
- **Error `400`:** Game `INACTIVE`.

#### `POST /game/{id}/finalize`
- **Response `200`:** `GameControlMessage` with `gameStatus: "FINALIZED"`
- **Error `409`:** Game not `ACTIVE` or already `ENDED`.

#### `POST /game/{id}/resume`
- **Response `200`:** `GameControlMessage` with `gameStatus: "ACTIVE"`
- **Error `409`:** Game not `FINALIZED`.

#### `POST /game/{id}/selection`
- **Body:** `SubstituteRemovalRequest`
- **Response `200`:** `GameControlMessage` with `gameStatus: "FINALIZED"`
- **Error `409`:** Game not `FINALIZED`, team already locked in, or removal count out of bounds.

#### `GET /game/{id}/selection/locked-in`
- **Response `200`:** `LockedInUpdate`
- **Error `409`:** Game is still `ACTIVE` or `INACTIVE`.

#### `GET /game/{id}/selection/{teamAssociation}`
- **Path param:** `teamAssociation` — e.g., `CSK` (case-insensitive)
- **Response `200`:** `Ranking` for the specified team
- **Error `409`:** Game not finalized, or team hasn't locked in yet.

#### `POST /game/{id}/end`
- **Response `200`:** `Ranking[]` (final rankings)
- **Note:** Logs a warning if not all 10 teams have locked in their selection.
- **Error `409`:** Game not `FINALIZED`.

#### `GET /game/{id}/results`
- **Response `200`:** `Ranking[]`
- **Error `409`:** Game not `ENDED`.

#### `POST /game/{id}/results/publish`
- **Response:** Empty body (`204`-equivalent)
- **Side effect:** Broadcasts `RESULT` WebSocket event to each team's subscription.
- **Error `409`:** Game not `ENDED`.

---

## WebSocket (STOMP)

### Connection

Both SockJS and raw WebSocket endpoints are available at `/ws`.

```javascript
// SockJS
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, onConnected);

// Raw WebSocket
const stompClient = Stomp.client('ws://localhost:8080/ws');
stompClient.connect({}, onConnected);
```

- **App destination prefix:** `/app`
- **Broker prefix:** `/topic`

### Send (Client → Server)

#### `SEND /app/game/{gameId}/bids`
- **Body:** `BidRequest` — `{ "currentBid": 750000 }`
- Broadcasts the current bid to all subscribers on `/topic/game/{gameId}/bids`.
- If the game is not `ACTIVE`, an `ERROR` event is broadcast to the bids topic instead.

```javascript
stompClient.send(
  '/app/game/1/bids',
  {},
  JSON.stringify({ currentBid: 750000 })
);
```

---

### Subscribe (Server → Client)

All messages are wrapped in `WebSocketEvent<T>`:
```json
{
  "eventType": "BID",
  "timestamp": "2026-02-22T12:34:56Z",
  "payload": { ... }
}
```

| Topic | Payload | `eventType` | Triggered by |
|---|---|---|---|
| `/topic/game/{gameId}/bids` | `BidRequest` | `BID` or `ERROR` | Client sends to `/app/game/{gameId}/bids` |
| `/topic/game/{gameId}/purchases/{association}` | `PurchasedPlayer` | `PURCHASE` | `POST /game/{id}/purchase` |
| `/topic/game/{gameId}/refunds/{association}` | `RefundConfirmation` | `REFUND` | `POST /game/{id}/refund` |
| `/topic/game/{gameId}/team/{association}` | `TeamDTO` | `TEAM_UPDATE` | Purchase or refund |
| `/topic/game/{gameId}/audit` | `GameLog` | `AUDIT` | Purchase or refund |
| `/game/{gameId}/results/{association}` | `Ranking` | `RESULT` | `POST /game/{id}/results/publish` |

> ⚠️ **Known issue:** The results topic (`/game/{gameId}/results/{association}`) intentionally omits the `/topic/` prefix. Subscribe to this exact path without `/topic/`.

#### Subscription Examples

```javascript
// Current bid for a game
stompClient.subscribe('/topic/game/1/bids', frame => {
  const { eventType, payload } = JSON.parse(frame.body);
  // eventType: "BID" | "ERROR"
});

// Purchases for a specific team
stompClient.subscribe('/topic/game/1/purchases/CSK', frame => {
  const { payload } = JSON.parse(frame.body); // PurchasedPlayer
});

// Team balance/slot updates
stompClient.subscribe('/topic/game/1/team/CSK', frame => {
  const { payload } = JSON.parse(frame.body); // TeamDTO
});

// Results (note: no /topic/ prefix)
stompClient.subscribe('/game/1/results/CSK', frame => {
  const { payload } = JSON.parse(frame.body); // Ranking
});
```
