#!/bin/bash

m1=mongo1
port=${PORT:-27017}

sleep 10
mongosh --tls --tlsCAFile /certs/ca.crt --tlsCertificateKeyFile /certs/primary.pem --host primary.mongodb.local:27017 <<EOF
#mongosh --host  primary.mongodb.local:27017 <<EOF
rs.status()

use voilist;
db = db.getSiblingDB('voilist');
db.createCollection('voilistData');
db.runCommand({collMod: "voilistData", changeStreamPreAndPostImages: { enabled: true }});
db.getCollectionInfos();
EOF

