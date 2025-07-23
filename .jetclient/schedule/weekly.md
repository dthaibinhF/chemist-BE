```toml
name = 'weekly'
method = 'POST'
url = '{{baseUrl}}/schedule/weekly?groupId=3&startDate=2025-07-22T17:00:00.000Z&endDate=2025-07-29T16:59:59.999Z'
sortWeight = 1000000
id = '7141708e-b002-446f-b10d-b9ba21ac771e'

[[queryParams]]
key = 'groupId'
value = '3'

[[queryParams]]
key = 'startDate'
value = '2025-07-22T17:00:00.000Z'

[[queryParams]]
key = 'endDate'
value = '2025-07-29T16:59:59.999Z'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
