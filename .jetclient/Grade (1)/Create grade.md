```toml
name = 'Create grade'
description = 'add grade'
method = 'POST'
url = '{{baseUrl}}/grade'
sortWeight = 1000000
id = '08854a6e-c02d-4c09-a648-cdb4520ccd93'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Test Grade"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
// Set the grade ID in the environment variable
jc.environment.set("gradeId", jc.response.json('id'));
console.log(jc.variables.get("gradeId"));
```
