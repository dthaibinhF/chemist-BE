```toml
name = 'simple chat'
method = 'POST'
url = '{{baseUrl}}/ai/chat'
sortWeight = 2000000
id = '74034956-c707-4875-9a7a-ad3164eb74fc'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "message": "Bạn Trần Huy Đạt đã đóng tiền học đủ chưa",
  "conversationId": '14034950',
}'''
```
