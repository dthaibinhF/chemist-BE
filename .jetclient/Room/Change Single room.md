```toml
name = 'Change Single room'
method = 'PUT'
url = '{{baseUrl}}/room/:id'
sortWeight = 2500000
id = 'c67de07b-e508-4862-910f-5edd048a7f8b'

[[pathVariables]]
key = 'id'
value = '{{roomId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Updated Test Room 102",
  "location": "Building B",
  "capacity": 40
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Room change success", function () {
    jc.expect(jc.response.json('name'), "Updated Test Room 102");
    jc.expect(jc.response.json('location'), "Building B");
    jc.expect(jc.response.json('capacity'), 40);
})
```
