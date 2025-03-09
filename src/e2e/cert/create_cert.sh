echo "Primary Node pem file creation..."
echo "Generate a private key 'ca.key' for CA cert"
openssl genrsa -out ca.key 2048
openssl req -x509 -new -nodes -key ca.key -sha256 -days 1024 -out ca.crt -subj "/C=US/ST=State/L=City/O=Organization/CN=mongodb.local"
openssl x509 -in ca.crt -text -noout | grep -A 1 "Subject Alternative Name"
openssl x509 -in ca.crt -text -noout | grep -A 1 "Extended Key Usage"
openssl genrsa -out primary.key 2048

echo "Create a CSR with the key"
openssl req -new -key primary.key -out primary.csr -subj "/C=US/ST=State/L=City/O=Organization/CN=primary.mongodb.local"

echo "Sign CSR with a CA cert"
openssl x509 -req -in primary.csr -CA ca.crt -CAkey ca.key -CAcreateserial -sha256 -days 1024 -out primary.crt -extensions v3_req -extfile openssl.cnf

echo "Verifying primary.crt SAN"
openssl x509 -in primary.crt -text -noout | grep -A 1 "Subject Alternative Name"

echo "Verifying primary.crt KeyUsage"
openssl x509 -in primary.crt -text -noout | grep -A 1 "Extended Key Usage"

cat primary.key primary.crt > primary.pem

echo "Replica1 Node pem file creation"
echo "Generate a private key 'ca.key' for CA cert"
openssl genrsa -out replica1.key 2048
echo "Create a CSR with the key"
openssl req -new -key replica1.key -out replica1.csr -subj "/C=US/ST=State/L=City/O=Organization/OU=IT/CN=replica1.mongodb.local"

echo "Sign CSR with a CA cert"
openssl x509 -req -in replica1.csr -CA ca.crt -CAkey ca.key -CAcreateserial -sha256 -days 1024 -out replica1.crt -extensions v3_req -extfile openssl.cnf

echo "Verifying replica1.crt SAN"
openssl x509 -in replica1.crt -text -noout | grep -A 1 "Subject Alternative Name"

echo "Verifying replica1.crt KeyUsage"
openssl x509 -in replica1.crt -text -noout | grep -A 1 "Extended Key Usage"

cat replica1.key replica1.crt > replica1.pem

echo "Replica2 Node pem file creation"
echo "Generate a private key 'replica2.key' for CA cert"
openssl genrsa -out replica2.key 2048
echo "Create a CSR with the key"
openssl req -new -key replica2.key -out replica2.csr -subj "/C=US/ST=State/L=City/O=Organization/OU=IT/CN=replica2.mongodb.local"

echo "Sign CSR with a CA cert"
openssl x509 -req -in replica2.csr -CA ca.crt -CAkey ca.key -CAcreateserial -sha256 -days 1024 -out replica2.crt -extensions v3_req -extfile openssl.cnf

echo "Verifying replica1.crt SAN"
openssl x509 -in replica2.crt -text -noout | grep -A 1 "Subject Alternative Name"

echo "Verifying replica1.crt KeyUsage"
openssl x509 -in replica2.crt -text -noout | grep -A 1 "Extended Key Usage"

cat replica2.key replica2.crt > replica2.pem

echo "creating jks file for jvm apps"
echo "convert pem to pkcs12"
openssl pkcs12 -export -in primary.pem -inkey primary.key -out primary.pkcs12 -password pass:mongo123

echo "create jks file with passphrase 'mongo123'"
keytool -importkeystore -srckeystore primary.pkcs12 -srcstoretype PKCS12 -destkeystore localmongo.jks -deststoretype JKS -srcstorepass mongo123 -deststorepass mongo123



