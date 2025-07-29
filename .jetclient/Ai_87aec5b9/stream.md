```toml
name = 'stream'
method = 'GET'
url = 'http://localhost:8080/api/v1/ai/chat/stream?message=hiện tại lớp 12 có bao nhiêu nhóm vậy&conversation_id=conv_1753757355589_l7c3te0eo'
sortWeight = 3000000
id = 'ef4b6679-eac1-4ace-afe0-31d8f80acc2d'

[[queryParams]]
key = 'message'
value = 'hiện tại lớp 12 có bao nhiêu nhóm vậy'

[[queryParams]]
key = 'conversation_id'
value = 'conv_1753757355589_l7c3te0eo'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[[headers]]
key = 'Content-Type'
value = 'text/event-stream'
```
