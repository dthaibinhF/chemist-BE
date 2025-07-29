```toml
name = 'chat'
method = 'GET'
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
  "message": "Bạn Trần Huy Đạt đã đóng tiền học đủ chưa",
  "conversationId": '14034950',
}'''
```
