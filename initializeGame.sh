curl --location 'localhost:6769/players/bulk' \
--header 'Content-Type: application/json' \
--data '[
  {
    "id": null,
    "name": "KL Rahul",
    "imageLink": "rahul.jpg",
    "type": "BATSMAN",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 4500, "matches": 130, "battingAvg": 45.3, "strikeRate": 88.1 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Shubman Gill",
    "imageLink": "gill.jpg",
    "type": "BATSMAN",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 2900, "matches": 85, "battingAvg": 42.7, "strikeRate": 92.4 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "David Warner",
    "imageLink": "warner.jpg",
    "type": "BATSMAN",
    "isForeign": true,
    "batsmanStatsDTO": { "runs": 6000, "matches": 160, "battingAvg": 41.2, "strikeRate": 95.6 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Babar Azam",
    "imageLink": "babar.jpg",
    "type": "BATSMAN",
    "isForeign": true,
    "batsmanStatsDTO": { "runs": 5000, "matches": 150, "battingAvg": 48.9, "strikeRate": 86.3 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Suryakumar Yadav",
    "imageLink": "sky.jpg",
    "type": "BATSMAN",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 2800, "matches": 95, "battingAvg": 38.6, "strikeRate": 104.2 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },

  {
    "id": null,
    "name": "Mohammed Shami",
    "imageLink": "shami.jpg",
    "type": "BOWLER",
    "isForeign": false,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": { "wickets": 190, "matches": 105, "bowlingAvg": 25.3, "economy": 5.6 },
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Rashid Khan",
    "imageLink": "rashid.jpg",
    "type": "BOWLER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": { "wickets": 170, "matches": 95, "bowlingAvg": 18.4, "economy": 4.9 },
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Trent Boult",
    "imageLink": "boult.jpg",
    "type": "BOWLER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": { "wickets": 190, "matches": 120, "bowlingAvg": 24.1, "economy": 5.3 },
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Yuzvendra Chahal",
    "imageLink": "chahal.jpg",
    "type": "BOWLER",
    "isForeign": false,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": { "wickets": 210, "matches": 145, "bowlingAvg": 21.8, "economy": 5.1 },
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Shaheen Afridi",
    "imageLink": "shaheen.jpg",
    "type": "BOWLER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": { "wickets": 115, "matches": 65, "bowlingAvg": 22.6, "economy": 5.8 },
    "allRounderStatsDTO": null
  },

  {
    "id": null,
    "name": "Rishabh Pant",
    "imageLink": "pant.jpg",
    "type": "WICKET_KEEPER",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 2800, "matches": 90, "battingAvg": 34.5, "strikeRate": 98.7 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Jos Buttler",
    "imageLink": "buttler.jpg",
    "type": "WICKET_KEEPER",
    "isForeign": true,
    "batsmanStatsDTO": { "runs": 4200, "matches": 150, "battingAvg": 40.1, "strikeRate": 102.4 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Quinton de Kock",
    "imageLink": "qdk.jpg",
    "type": "WICKET_KEEPER",
    "isForeign": true,
    "batsmanStatsDTO": { "runs": 5100, "matches": 155, "battingAvg": 44.8, "strikeRate": 96.2 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Ishan Kishan",
    "imageLink": "ishan.jpg",
    "type": "WICKET_KEEPER",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 2500, "matches": 85, "battingAvg": 32.9, "strikeRate": 99.5 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },
  {
    "id": null,
    "name": "Sanju Samson",
    "imageLink": "samson.jpg",
    "type": "WICKET_KEEPER",
    "isForeign": false,
    "batsmanStatsDTO": { "runs": 3000, "matches": 110, "battingAvg": 36.4, "strikeRate": 92.3 },
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": null
  },

  {
    "id": null,
    "name": "Hardik Pandya",
    "imageLink": "hardik.jpg",
    "type": "ALL_ROUNDER",
    "isForeign": false,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": { "runs": 2300, "wickets": 80, "matches": 100, "strikeRate": 98.4 }
  },
  {
    "id": null,
    "name": "Ravindra Jadeja",
    "imageLink": "jadeja.jpg",
    "type": "ALL_ROUNDER",
    "isForeign": false,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": { "runs": 2600, "wickets": 190, "matches": 140, "strikeRate": 87.6 }
  },
  {
    "id": null,
    "name": "Glenn Maxwell",
    "imageLink": "maxwell.jpg",
    "type": "ALL_ROUNDER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": { "runs": 3800, "wickets": 85, "matches": 160, "strikeRate": 102.3 }
  },
  {
    "id": null,
    "name": "Shakib Al Hasan",
    "imageLink": "shakib.jpg",
    "type": "ALL_ROUNDER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": { "runs": 4500, "wickets": 280, "matches": 210, "strikeRate": 89.4 }
  },
  {
    "id": null,
    "name": "Marcus Stoinis",
    "imageLink": "stoinis.jpg",
    "type": "ALL_ROUNDER",
    "isForeign": true,
    "batsmanStatsDTO": null,
    "bowlerStatsDTO": null,
    "allRounderStatsDTO": { "runs": 3100, "wickets": 65, "matches": 120, "strikeRate": 96.8 }
  }
]'
curl --location 'localhost:6769/set' \
--header 'Content-Type: application/json' \
--data '{
    "name":"Mike'\''s Set"
}'
curl --location 'localhost:6769/set/1/bulk' \
--header 'Content-Type: application/json' \
--data '[
  { "setId": 1, "playerId": 1, "points": 850, "price": 12.50, "order": 1 },
  { "setId": 1, "playerId": 2, "points": 820, "price": 11.75, "order": 2 },
  { "setId": 1, "playerId": 3, "points": 900, "price": 13.50, "order": 3 },
  { "setId": 1, "playerId": 4, "points": 880, "price": 13.00, "order": 4 },
  { "setId": 1, "playerId": 5, "points": 840, "price": 12.00, "order": 5 },

  { "setId": 1, "playerId": 6, "points": 890, "price": 12.75, "order": 6 },
  { "setId": 1, "playerId": 7, "points": 920, "price": 14.00, "order": 7 },
  { "setId": 1, "playerId": 8, "points": 870, "price": 12.25, "order": 8 },
  { "setId": 1, "playerId": 9, "points": 860, "price": 11.90, "order": 9 },
  { "setId": 1, "playerId": 10, "points": 880, "price": 12.40, "order": 10 },

  { "setId": 1, "playerId": 11, "points": 830, "price": 11.50, "order": 11 },
  { "setId": 1, "playerId": 12, "points": 900, "price": 13.20, "order": 12 },
  { "setId": 1, "playerId": 13, "points": 890, "price": 13.00, "order": 13 },
  { "setId": 1, "playerId": 14, "points": 810, "price": 11.25, "order": 14 },
  { "setId": 1, "playerId": 15, "points": 820, "price": 11.40, "order": 15 },

  { "setId": 1, "playerId": 16, "points": 910, "price": 14.50, "order": 16 },
  { "setId": 1, "playerId": 17, "points": 900, "price": 14.00, "order": 17 },
  { "setId": 1, "playerId": 18, "points": 920, "price": 15.00, "order": 18 },
  { "setId": 1, "playerId": 19, "points": 930, "price": 15.50, "order": 19 },
  { "setId": 1, "playerId": 20, "points": 880, "price": 13.75, "order": 20 }
]
'
curl --location 'localhost:6769/game' \
--header 'Content-Type: application/json' \
--data '{
    "setId":1,
    "name":"Mike'\''s Game",
    "initialBalance":10000000
}'