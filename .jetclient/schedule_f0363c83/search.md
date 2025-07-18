```toml
name = 'search'
method = 'GET'
url = '{{baseUrl}}/schedule/search?groupId=1&startDate&endDate'
sortWeight = 1000000
id = '8c0d6399-f02e-4f84-8160-f91f8156c6ad'

[[queryParams]]
key = 'groupId'
value = '1'

[[queryParams]]
key = 'startDate'

[[queryParams]]
key = 'endDate'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
