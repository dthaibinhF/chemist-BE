```toml
name = 'AFTER DELETE: single school'
method = 'GET'
url = '{{baseUrl}}/school/:id'
sortWeight = 5000000
id = '4c4c351a-557f-481f-a0cb-0d6598b22faa'

[[pathVariables]]
key = 'id'
value = '{{schoolId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("FAILED TEST: status code is 404", function () {
    jc.expect(jc.response.status, 404);
})

jc.test("FAILED TEST: School not Exist", function () {
    jc.expect(jc.response.json('error')).to.include("School not found");
})
```