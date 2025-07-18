```toml
name = 'Change Single school'
method = 'PUT'
url = '{{baseUrl}}/school/:id'
sortWeight = 2500000
id = '967de07b-e508-4862-910f-5edd048a7f8b'

[[pathVariables]]
key = 'id'
value = '{{schoolId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Updated Test School"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("School change success", function () {
    jc.expect(jc.response.json('name'), "Updated Test School");
})
```