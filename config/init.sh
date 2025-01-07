#!/bin/bash

echo "Stopping and removing existing containers..."
docker-compose down -v

echo "Starting containers..."
docker-compose up -d

echo "Waiting for services to start..."
sleep 30

echo "Initializing config servers replica set..."
docker exec mongodb_configsvr1 mongosh --eval '
rs.initiate({
  _id: "configReplSet",
  members: [
    {_id: 0, host: "mongodb_configsvr1:27017"},
    {_id: 1, host: "mongodb_configsvr2:27017"},
    {_id: 2, host: "mongodb_configsvr3:27017"}
  ]
})'

sleep 20

echo "Initializing shard 1 replica set..."
docker exec mongodb_shard1 mongosh --eval '
rs.initiate({
  _id: "shard1",
  members: [
    {_id: 0, host: "mongodb_shard1:27017", priority: 2},
    {_id: 1, host: "mongodb_shard1_secondary1:27017", priority: 1},
    {_id: 2, host: "mongodb_shard1_secondary2:27017", priority: 1}
  ]
})'

echo "Initializing shard 2 replica set..."
docker exec mongodb_shard2 mongosh --eval '
rs.initiate({
  _id: "shard2",
  members: [
    {_id: 0, host: "mongodb_shard2:27017", priority: 2},
    {_id: 1, host: "mongodb_shard2_secondary1:27017", priority: 1},
    {_id: 2, host: "mongodb_shard2_secondary2:27017", priority: 1}
  ]
})'

sleep 30

echo "Adding shards to cluster..."
docker exec mongodb_router mongosh --eval '
sh.addShard("shard1/mongodb_shard1:27017");
sh.addShard("shard2/mongodb_shard2:27017");'

echo "Setting up bigdata database..."
docker exec mongodb_router mongosh --eval '
use bigdata;
sh.enableSharding("bigdata");
db.products.createIndex({ region: 1 });
sh.shardCollection("bigdata.products", { region: 1 });'

echo "Verifying cluster status..."
docker exec mongodb_router mongosh --eval 'sh.status()'