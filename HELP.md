# Getting Started

### Reference Documentation

    docker exec -it primary.mongodb.local mongosh --tls --tlsCAFile /certs/ca.crt --tlsCertificateKeyFile /certs/primary.pem voilist
    db.voilistData.countDocuments();
    db.voilistData.deleteMany({});


### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

