```toml
name = 'search Student'
method = 'GET'
url = '{{baseUrl}}/student/search?studentName=đạt&page&size&sort&groupName=12NC3&schoolName=lý tự trọng&className&parentPhone'
sortWeight = 6000000
id = '41e87d4e-a2a8-4cbf-b4c6-cef7d5db17f7'

[[queryParams]]
key = 'studentName'
value = 'đạt'

[[queryParams]]
key = 'page'

[[queryParams]]
key = 'size'

[[queryParams]]
key = 'sort'

[[queryParams]]
key = 'groupName'
value = '12NC3'

[[queryParams]]
key = 'schoolName'
value = 'lý tự trọng'

[[queryParams]]
key = 'className'

[[queryParams]]
key = 'parentPhone'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```
