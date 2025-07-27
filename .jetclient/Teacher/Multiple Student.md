```toml
name = 'Multiple Student'
method = 'POST'
url = '{{baseUrl}}/student/multiple'
sortWeight = 1000000
id = '3ef463e6-b218-45a8-bab2-18219b1720c1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
[
  {
    "name": "Nguyễn Ngọc Thiên Lý ",
    "student_details": [
      {
        "school": {
          "name": "Trường THPT Bình Minh",
          "id": 7,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "academic_year": {
          "year": "2025-2026",
          "id": 4,
          "create_at": "2025-05-19T00:00:00+07:00"
        },
        "group_id": "14",
        "grade": {
          "name": "12",
          "id": 12,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "student_name": "Nguyễn Ngọc Thiên Lý "
      }
    ]
  },
  {
    "name": "Nguyễn Trần Huế Ngọc",
    "student_details": [
      {
        "school": {
          "name": "Trường THPT Bình Minh",
          "id": 7,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "academic_year": {
          "year": "2025-2026",
          "id": 4,
          "create_at": "2025-05-19T00:00:00+07:00"
        },
        "group_id": "14",
        "grade": {
          "name": "12",
          "id": 12,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "student_name": "Nguyễn Trần Huế Ngọc"
      }
    ]
  },
  {
    "name": "Minh Trần Bảo Long",
    "student_details": [
      {
        "school": {
          "name": "Trường THPT Lý Tự Trọng",
          "id": 8,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "academic_year": {
          "year": "2025-2026",
          "id": 4,
          "create_at": "2025-05-19T00:00:00+07:00"
        },
        "group_id": "14",
        "grade": {
          "name": "12",
          "id": 12,
          "create_at": "2024-09-05T00:00:00+07:00"
        },
        "student_name": "Minh Trần Bảo Long"
      }
    ]
  }
]'''
```
