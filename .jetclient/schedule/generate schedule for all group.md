```toml
name = 'generate schedule for all group'
method = 'POST'
url = '{{baseUrl}}/schedule/bulk/all-groups?startDate=2025-08-11&endDate=2025-08-16'
sortWeight = 5000000
id = '11afe39f-f0df-4064-bc0d-f17778834cc2'

[[queryParams]]
key = 'startDate'
value = '2025-08-11'

[[queryParams]]
key = 'endDate'
value = '2025-08-16'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
