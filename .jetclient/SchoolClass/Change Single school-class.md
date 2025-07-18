```toml
name = 'Change Single school-class'
method = 'PUT'
url = '{{baseUrl}}/school-class/:id'
sortWeight = 2500000
id = 'b67de07b-e508-4862-910f-5edd048a7f8b'

[[pathVariables]]
key = 'id'
value = '{{schoolClassId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Updated Class"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.response.to.have.status(200);
})

jc.test("School Class change success", function () {
    jc.expect(jc.response.json('name'), "Updated Class");
})
```
