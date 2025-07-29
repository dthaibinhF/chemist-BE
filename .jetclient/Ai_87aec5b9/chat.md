```toml
name = 'chat'
method = 'POST'
url = '{{baseUrl}}/ai/chat'
sortWeight = 5000000
id = 'ce71339a-a29f-473d-a820-41f4c193f3f1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "message": "hiện tại lớp 12 có bao nhiêu nhóm vậy",
  "conversationId": '14034900',
}'''
```
