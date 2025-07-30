```toml
name = 'create All payment summary for group'
method = 'POST'
url = '{{baseUrl}}/student-payment/group/:groupId/generate-all'
sortWeight = 4000000
id = '3f34c898-a0fa-4af0-806b-bc7ba510e9c9'

[[pathVariables]]
key = 'groupId'
value = '11'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
```
