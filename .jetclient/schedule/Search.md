```toml
name = 'Search'
method = 'GET'
url = '{{baseUrl}}/schedule/search?groupId=1&startDate=2025-07-23&endDate=2025-07-30'
sortWeight = 2000000
id = '7e982a65-3593-4f0a-b3db-7b429bb9250d'

[[queryParams]]
key = 'groupId'
value = '1'

[[queryParams]]
key = 'startDate'
value = '2025-07-23'

[[queryParams]]
key = 'endDate'
value = '2025-07-30'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
