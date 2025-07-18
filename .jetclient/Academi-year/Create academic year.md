```toml
name = 'Create academic year'
description = 'add year'
method = 'POST'
url = '{{baseUrl}}/academic-year'
sortWeight = 1000000
id = '6a872026-64fa-40c6-9ddf-db4e998761c9'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "year": "2026-2027"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
// Set the academic year ID in the environment variable
jc.environment.set("academicYearId", jc.response.json('id'));
console.log(jc.variables.get("academicYearId"));



```
