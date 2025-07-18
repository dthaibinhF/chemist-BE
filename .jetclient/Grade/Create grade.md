```toml
name = 'Create grade'
description = 'add grade'
method = 'POST'
url = '{{baseUrl}}/grade'
sortWeight = 1000000
id = '8a872026-64fa-40c6-9ddf-db4e998761c9'

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