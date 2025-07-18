```toml
name = 'Change Single grade'
method = 'PUT'
url = '{{baseUrl}}/grade/:id'
sortWeight = 2500000
id = 'a67de07b-e508-4862-910f-5edd048a7f8b'

[[pathVariables]]
key = 'id'
value = '{{gradeId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Updated Test Grade"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Grade change success", function () {
    jc.expect(jc.response.json('name'), "Updated Test Grade");
})
```