```toml
name = 'student-payment/student/id'
method = 'POST'
url = '{{baseUrl}}/student-payment/student/:studentId/group/:groupId'
sortWeight = 3000000
id = 'f558f3a9-62c6-4121-91fc-367a941ae884'

[[pathVariables]]
key = 'studentId'
value = '6'

[[pathVariables]]
key = 'groupId'
value = '1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
```
