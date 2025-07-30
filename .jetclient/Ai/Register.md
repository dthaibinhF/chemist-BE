```toml
name = 'Register'
method = 'POST'
url = 'http://localhost:8080/api/v1/auth/register'
sortWeight = 2000000
id = '7b80ea29-1a49-463c-b202-023f576abbb8'

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
