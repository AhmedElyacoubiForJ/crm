# Testing-Dokumentation

## HTTP-Anfragen mit cURL
- **Stelle sicher, dass [cURL](https://curl.se/download.html) installiert ist:**
   ```sh
   curl --version
   ```
- Verwende cURL, um HTTP-Anfragen an unserem Server zu senden.
    - `GET-Anfrage`:
      ```sh
      curl -X GET http://localhost:8080/api/resources
      ```
    -  `POST-Anfrage`:
       ```sh
       curl -X POST -H "Content-Type: application/json" -d '{"key":"value"}' http://localhost:8080/api/resources

### Get-Anfrage

```sh
curl -X GET http://localhost:8080/api/v1/resources
```
- Anfrage für nicht existierenden Employee 
```sh
        curl -X GET http://localhost:8080/api/employees/22
```
- Beispiel Antwort (nicht existierender Employee):
```
  {
  "statusCode": 404,
  "message": "Employee not found with ID: 22"
  }
```
- POST-Anfrage zur Erstellung eines neuen Employees
```sh
  curl -X POST http://localhost:8080/api/employees \
     -H "Content-Type: application/json" \
     -d '{
           "firstName": "John",
           "lastName": "Doe",
           "email": "john.doe@example.com",
           "department": "Sales"
         }'
```
- Beispiel Antwort (neuer Employee):
```
  {
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Sales"
}
```
- PUT-Anfrage zur Aktualisierung eines bestehenden Employees:
```sh
  curl -X PUT http://localhost:8080/api/employees/1 \
     -H "Content-Type: application/json" \
     -d '{
           "firstName": "Jane",
           "lastName": "Doe",
           "email": "jane.doe@example.com",
           "department": "HR"
         }'
```
- Beispiel Antwort (aktualisierter Employee):
```
  {
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "department": "HR"
 }
```

- POST-Anfrage zur Erstellung eines customers
```sh
   curl -X POST "http://localhost:8080/api/customers?employeeId=1" -H "Content-Type: application/json" -d '{
  "firstName": "Max",
  "firstName": "Max",
  "lastName": "Mustermann",
  "email": "max.mustermann@example.com",
  "lastInteractionDate": "2023-12-05T12:34:56Z"
}'
```
- Beispiel Antwort (neue Customer):
```
  {
  "id":1,
  "firstName":"Max",
  "lastName":"Mustermann",
  "email":"max.mustermann@example.com",
  "phone":null,
  "address":null,
  "lastInteractionDate":"2023-12-05",
  "employeeId":1
  }
```
- PUT-Anfrage zum Aktualisieren eines customers
```sh
   curl -X PUT "http://localhost:8080/api/customers/1/updateByExample" -H "Content-Type: application/json; charset=utf-8" -d @customerDTO.json
}'
```
- Beispiel Antwort (neue Customer):
```
  {
  "id":1,
  "firstName":"Max Updated",
  "lastName":"Mustermann",
  "email":"max_updated.mustermann@example.com",
  "phone":null,
  "address":"Neue Straße 123",
  "lastInteractionDate":"2023-12-05",
  "employeeId":1
  }
```
- POST (eine neue Notiz erstellen):
```sh
   curl -X POST "http://localhost:8080/api/notes?customerId=1" -H "Content-Type: application/json" -d '{
  "content": "Meeting with client",
  "date": "2023-12-01",
  "interactionType": "MEETING"
}'

```
- Beispiel Antwort (neue Customer):
```
  {
  "id":1,
  "content":"Meeting with client",
  "date":"2023-12-01",
  "interactionType":"MEETING",
  "customerId":1
  }
```
- PUT (eine Notiz aktualisieren):
```sh
   curl -X PUT "http://localhost:8080/api/notes/1" -H "Content-Type: application/json" -d '{
  "content": "Updated meeting with client",
  "date": "2023-12-02",
  "interactionType": "PHONE_CALL"
}'


```
- Beispiel Antwort (note update):
```
  {
  "id":1,
  "content":"Updated meeting with client",
  "date":"2023-12-02",
  "interactionType":"PHONE_CALL",
  "customerId":1
  }
```
- DELETE (eine Notiz löschen):
```sh
   curl -i -X DELETE "http://localhost:8080/api/notes/1"
```
- Beispiel Antwort (note delete):
```
  HTTP/1.1 204 
  Date: Tue, 22 Oct 2024 01:06:35 GMT
```
- GET (eine Notiz abrufen):
```sh
   curl -X GET "http://localhost:8080/api/notes/1"
```
- Beispiel Antwort (eine Notiz abrufen):
```
  {
  "id":5,
  "content":"Meeting with client",
  "date":"2023-12-01",
  "interactionType":"MEETING",
  "customerId":1
  }
```