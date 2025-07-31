```toml
name = 'simple chat'
method = 'POST'
url = '{{baseUrl}}/ai/chat/simple'
sortWeight = 2000000
id = '74034956-c707-4875-9a7a-ad3164eb74fc'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "message": "hiện tại lớp 12 có bao nhiêu nhóm vậy",
  "conversationId": '36034950',
}'''
```
