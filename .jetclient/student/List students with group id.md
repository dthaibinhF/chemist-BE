```toml
name = 'List students with group id'
method = 'GET'
url = '{{baseUrl}}/student/by-group/:group-id'
sortWeight = 2000000
id = 'fe3db4b5-ac2d-4254-bcd1-0f2e07596b9c'

[[pathVariables]]
key = 'group-id'
value = '3'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
