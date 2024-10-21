# Testing-Dokumentation

## HTTP-Anfragen mit cURL

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