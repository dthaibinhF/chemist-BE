```toml
name = 'stream'
method = 'GET'
url = 'http://localhost:8080/api/v1/ai/chat/stream?message=hi, what can you do&conversation_id=conv_1753759846363_jffjks86n'
sortWeight = 3000000
id = 'ef4b6679-eac1-4ace-afe0-31d8f80acc2d'

[[queryParams]]
key = 'message'
value = 'hi, what can you do'

[[queryParams]]
key = 'conversation_id'
value = 'conv_1753759846363_jffjks86n'

[[queryParams]]
value = 'Lớp 12 có bao nhiêu nhóm và mấy giờ vậy'
disabled = true

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[[headers]]
key = 'Content-Type'
value = 'text/event-stream'
```
