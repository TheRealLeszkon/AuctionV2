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
    "playersPerTeam":1,
    "batsmenPerTeam":1,
    "bowlersPerTeam":0,
    "allRounderPerTeam":0,
    "wicketKeeperPerTeam":0,
    "substitutesPerTeam":1,
    "unCappedPerTeam":0,
    "legendsPerTeam": 0,
    "specialPlayersPerTeam": 0,
    "foreignPlayersPerTeam":0
}'