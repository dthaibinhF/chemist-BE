```toml
name = 'Create school'
description = 'add school'
method = 'POST'
url = '{{baseUrl}}/school'
sortWeight = 1000000
id = '7a872026-64fa-40c6-9ddf-db4e998761c9'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Test School"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
// Set the school ID in the environment variable
jc.environment.set("schoolId", jc.response.json('id'));
console.log(jc.variables.get("schoolId"));
```