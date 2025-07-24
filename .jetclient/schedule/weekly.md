```toml
name = 'weekly'
method = 'POST'
url = '{{baseUrl}}/schedule/weekly?groupId=2&startDate=2025-07-24&endDate=2025-07-30'
sortWeight = 1000000
id = '7141708e-b002-446f-b10d-b9ba21ac771e'

[[queryParams]]
key = 'groupId'
value = '2'

[[queryParams]]
key = 'startDate'
value = '2025-07-24'

[[queryParams]]
key = 'endDate'
value = '2025-07-30'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
