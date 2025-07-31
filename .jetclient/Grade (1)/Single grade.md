```toml
name = 'Single grade'
method = 'GET'
url = '{{baseUrl}}/grade/:id'
sortWeight = 2000000
id = '10cb8ca6-6840-4c9b-b20a-019ea64da339'

[[pathVariables]]
key = 'id'
value = '{{gradeId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Grade Exists", function () {
    jc.expect(jc.response.json('id'), jc.variables.get("gradeId"));
})
```
