```toml
name = 'List students with group id'
method = 'GET'
url = '{{baseUrl}}/student/by-group/:group-id'
sortWeight = 3000000
id = 'b4624615-5a6b-4ca1-a239-74c97ca7efe3'

[[pathVariables]]
key = 'group-id'
value = '3'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
