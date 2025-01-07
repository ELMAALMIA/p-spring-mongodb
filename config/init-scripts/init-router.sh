docker exec mongodb_mongos mongosh --port 27017 --eval '
sh.addShard("shard1/mongodb_shard1:27020");
sh.addShard("shard2/mongodb_shard2:27021");
sh.enableSharding("bigdata");
use bigdata;
db.products.createIndex({ region: 1 });
sh.shardCollection("bigdata.products", { region: 1 });'