#!/bin/bash
docker exec mongodb_configsvr mongosh --port 27019 --eval '
rs.initiate({
  _id: "configReplSet",
  members: [
    { _id: 0, host: "mongodb_configsvr:27019" }
  ]
})'

# init-shards.sh
docker exec mongodb_shard1 mongosh --port 27020 --eval '
rs.initiate({
  _id: "shard1",
  members: [
    { _id: 0, host: "mongodb_shard1:27020" }
  ]
})'
