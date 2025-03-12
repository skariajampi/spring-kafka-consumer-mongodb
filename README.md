

docker exec -it primary.mongodb.local mongosh --tls --tlsCertificateKeyFile /certs/primary.pem --tlsCAFile /certs/ca.crt somelist

db.getCollectionInfos();
db.someListData.find();
db.someListData.countDocuments();
db.someListData.deleteMany({});