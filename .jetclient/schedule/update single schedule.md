```toml
name = 'update single schedule'
method = 'PUT'
url = '{{baseUrl}}/schedule/:id'
sortWeight = 4000000
id = '3550354f-0284-4c90-b626-1d1dad92ab95'

[[pathVariables]]
key = 'id'
value = '20'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "group_id": 3,
  "start_time": "2025-07-25T12:20:00.000Z",
  "end_time": "2025-07-25T14:00:00.000Z",
  "delivery_mode": "OFFLINE",
  "meeting_link": "",
  "teacher": {
    "account": {
      "id": 3
    }
  },
  "room": {
    "id": 1
  }
}'''
```
