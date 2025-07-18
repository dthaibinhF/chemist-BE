```toml
name = 'List students with group id'
method = 'GET'
url = '{{baseUrl}}/student/by-group/:group-id'
sortWeight = 3000000
id = 'a5ba53f0-db97-459a-aeb4-e35f20b9a8c0'

[[pathVariables]]
key = 'group-id'
value = '3'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
