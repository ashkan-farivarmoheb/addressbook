# addressbook


# Run in locally
1- ./gradlew clean build -x test
2- ./gradlew startTestEnvironment
3- ./gradlew bootRun --args='--spring.profiles.active=dev'

# Run in application via docker-compose
1- docker build -t ashkanfarivarmoheb/addressbook:0.0.1 .
2- docker-compose -f src/main/docker/compose/dev/docker-compose-dev.yml up -d

# Sample APIS

## Create an Address Book
curl --location 'http://localhost:8090/ms-address-book/v1/addressbooks' \
--header 'x-correlation-id: 12aaa456-3bb4-b31a-b31a-b31abbbaaa34' \
--header 'x-username: ava' \
--header 'Content-Type: application/json' \
--data '{
    "addressBookName": "c"
}'

## Create a contact
curl --location 'http://localhost:8090/ms-address-book/v1/addressbooks/{addressId}/contacts' \
--header 'x-correlation-id: 12aaa456-3bb4-b31a-b31a-b31abbbaaa34' \
--header 'x-username: ava' \
--header 'Content-Type: application/json' \
--data '{
    "firstname": "hiva",
    "lastname": "kordian",
    "phoneNumber": "+61(02)89886551",
    "type": "MOBILE"
}'

## Delete a contact
curl --location --request DELETE 'http://localhost:8090/ms-address-book/v1/addressbooks/{addressId}/contact/{contactId}' \
--header 'x-correlation-id: 12aaa456-3bb4-b31a-b31a-b31abbbaaa34' \
--header 'x-username: ava'

## Get List of contact for an address book
curl --location 'http://localhost:8090/ms-address-book/v1/addressbooks/2/contacts?size=2&sort=firstname%2Cdesc' \
--header 'x-correlation-id: 12aaa456-3bb4-b31a-b31a-b31abbbaaa34' \
--header 'x-username: ava'

## Get unique set of all contacts across multiple address books
curl --location 'http://localhost:8090/ms-address-book/v1/addressbooks/contacts?size=3&sort=firstname%2Cdesc' \
--header 'x-correlation-id: 12aaa456-3bb4-b31a-b31a-b31abbbaaa34' \
--header 'x-username: ava'

# Acutators

## health
http://localhost:8090/ms-address-book/actuator/health

## readiness
http://localhost:8090/ms-address-book/actuator/health/readiness

## liveness
http://localhost:8090/ms-address-book/actuator/health/liveness
