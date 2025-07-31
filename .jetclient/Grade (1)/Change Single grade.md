```toml
name = 'Change Single grade'
method = 'PUT'
url = '{{baseUrl}}/grade/:id'
sortWeight = 2500000
id = '4341840c-59cb-4b7e-bf7f-15fd36b392ea'

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
