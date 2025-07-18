```toml
name = 'Create room'
description = 'add room'
method = 'POST'
url = '{{baseUrl}}/room'
sortWeight = 1000000
id = 'aa872026-64fa-40c6-9ddf-db4e998761c9'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Test Room 101",
  "location": "Building A",
  "capacity": 30
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
// Set the room ID in the environment variable
jc.environment.set("roomId", jc.response.json('id'));
console.log(jc.variables.get("roomId"));
```
