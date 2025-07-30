```toml
name = 'Register'
method = 'GET'
url = 'http://localhost:8080/actuator/health'
sortWeight = 2000000
id = '05ac463d-de4f-4ff0-8e8b-b2ea151832b5'

[body]
type = 'JSON'
raw = '''
{
  "name": "Tester",
  "email": "test@gamil.com",
  "password": "Test@1234",
  "phone": "+84 939 464 077",
  "role_name": "ROLE_TESTER", //as tester
}'''
```
