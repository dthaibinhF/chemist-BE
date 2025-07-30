```toml
name = 'search Student'
method = 'GET'
url = '{{baseUrl}}/student/search?studentName=minh anh&page&size&sort&groupName&schoolName&className&parentPhone'
sortWeight = 6000000
id = '41e87d4e-a2a8-4cbf-b4c6-cef7d5db17f7'

[[queryParams]]
key = 'studentName'
value = 'minh anh'

[[queryParams]]
key = 'page'

[[queryParams]]
key = 'size'

[[queryParams]]
key = 'sort'

[[queryParams]]
key = 'groupName'

[[queryParams]]
key = 'schoolName'

[[queryParams]]
key = 'className'

[[queryParams]]
key = 'parentPhone'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
