```toml
name = 'search Student'
method = 'GET'
url = '{{baseUrl}}/student/search?studentName=đạt&page&size&sort&groupName=12NC3&schoolName=lý tự trọng&className&parentPhone'
sortWeight = 6000000
id = '673184b7-ad0f-47b9-a4db-36ae1157de02'

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
