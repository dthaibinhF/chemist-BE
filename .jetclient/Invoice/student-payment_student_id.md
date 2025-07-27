```toml
name = 'student-payment/student/id'
method = 'GET'
url = '{{baseUrl}}/student-payment/student/:id'
sortWeight = 3000000
id = 'f558f3a9-62c6-4121-91fc-367a941ae884'

[[pathVariables]]
key = 'id'
value = '106'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
```
