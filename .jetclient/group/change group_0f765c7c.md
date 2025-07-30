```toml
name = 'change group'
method = 'PUT'
url = '{{baseUrl}}/group/:id?syncFutureSchedules=true'
sortWeight = 1000000
id = '0f765c7c-f98c-47dd-812b-add61a878f69'

[[queryParams]]
key = 'syncFutureSchedules'
value = 'true'

[[pathVariables]]
key = 'id'
value = '1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "12NC1",
  "level": "ADVANCED",
  "fee_id": 1,
  "fee_name": "học phí hè 12 2025-2026",
  "academic_year_id": 4,
  "academic_year": "2025-2026",
  "grade_id": 12,
  "grade_name": "12",
  "group_schedules": [
    {
      "id": 52,
      "group_id": 1,
      "group_name": "12NC1",
      "day_of_week": "MONDAY",
      "start_time": "17:20:00",
      "end_time": "19:00:00",
      "room_id": 1,
      "room_name": "Phòng lớn trệt",
      "create_at": "2025-07-25T14:51:58.572749+07:00",
      "update_at": null,
      "end_at": null
    },
    {
      "id": 54,
      "group_id": 1,
      "group_name": "12NC1",
      "day_of_week": "WEDNESDAY",
      "start_time": "17:20:00",
      "end_time": "19:00:00",
      "room_id": 1,
      "room_name": "Phòng lớn trệt",
      "create_at": "2025-07-25T14:51:58.738596+07:00",
      "update_at": null,
      "end_at": null
    },
    {
      "id": 53,
      "group_id": 1,
      "group_name": "12NC1",
      "day_of_week": "SATURDAY",
      "start_time": "09:20:00",
      "end_time": "11:00:00",
      "room_id": 1,
      "room_name": "Phòng lớn trệt",
      "create_at": "2025-07-25T14:51:58.681192+07:00",
      "update_at": null,
      "end_at": null
    },
  ],
}'''
```
