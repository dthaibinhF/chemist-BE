```toml
name = 'AFTER DELETE: single grade'
method = 'GET'
url = '{{baseUrl}}/grade/:id'
sortWeight = 5000000
id = '7fdcf350-143c-435b-a834-231192b93526'

[[pathVariables]]
key = 'id'
value = '{{gradeId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("FAILED TEST: status code is 404", function () {
    jc.expect(jc.response.status, 404);
})

jc.test("FAILED TEST: Grade not Exist", function () {
    jc.expect(jc.response.json('error')).to.include("Grade not found");
})
```
