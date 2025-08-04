```toml
name = 'List student (1)'
method = 'GET'
url = '{{baseUrl}}/student-payment/student/:studentId/fee/:feeId'
sortWeight = 7000000
id = 'a3d8eee9-c7fd-4ae1-aa5a-f2ef01c397de'

[[pathVariables]]
key = 'studentId'
value = '106'

[[pathVariables]]
key = 'feeId'
value = '1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
