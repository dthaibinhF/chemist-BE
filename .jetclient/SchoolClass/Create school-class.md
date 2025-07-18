```toml
name = 'Create school-class'
description = 'add school class'
method = 'POST'
url = '{{baseUrl}}/school-class'
sortWeight = 1000000
id = '9a872026-64fa-40c6-9ddf-db4e998761c9'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Test Class"
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.response.to.have.status(200)
})
// Set the school class ID in the environment variable
jc.environment.set("schoolClassId", jc.response.json('id'));
console.log(jc.variables.get("schoolClassId"));
```
