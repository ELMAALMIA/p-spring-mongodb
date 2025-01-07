#!/bin/bash
echo "Initializing Config Server..."
sleep 10

mongosh --host localhost --port 27019 <<EOF
rs.initiate({
  _id: "configReplSet",
  members: [
    { _id: 0, host: "localhost:27019" }
  ]
})
EOF