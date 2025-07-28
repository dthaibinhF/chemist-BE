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
  "message": "yes i want to know about the fee",
  "conversationId": '74034950'
}'''
```
