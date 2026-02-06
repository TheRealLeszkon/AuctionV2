curl --location 'localhost:6769/players/bulk' \
--header 'Content-Type: application/json' \
--data @TestPlayerData.json
curl --location 'localhost:6769/set' \
--header 'Content-Type: application/json' \
--data '{
    "name":"Mike'\''s Set"
}'
curl --location 'localhost:6769/set/1/bulk' \
--header 'Content-Type: application/json' \
--data @TestSet.json
curl --location 'localhost:6769/game' \
--header 'Content-Type: application/json' \
--data '{
    "setId":1,
    "name":"Mike'\''s Game",
    "initialBalance":1200000000,
    "playersPerTeam":12,
    "batsmenPerTeam":4,
    "bowlersPerTeam":4,
    "allRounderPerTeam":2,
    "wicketKeeperPerTeam":1,
    "substitutesPerTeam":3,
    "unCappedPerTeam":1,
    "legendsPerTeam": 1,
    "specialPlayersPerTeam": 2,
    "foreignPlayersPerTeam":1
}'