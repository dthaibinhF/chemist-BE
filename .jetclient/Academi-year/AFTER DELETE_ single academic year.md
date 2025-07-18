```toml
name = 'AFTER DELETE: single academic year'
method = 'GET'
url = '{{baseUrl}}/academic-year/:id'
sortWeight = 5000000
id = '3c4c351a-557f-481f-a0cb-0d6598b22faa'

[[pathVariables]]
key = 'id'
value = '{{academicYearId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("FAILD TEST: status code is 404", function () {
    jc.expect(jc.response.status, 404);
})

jc.test("FAILD TEST: Academic not Exist", function () {
        jc.expect(jc.response.json('error'), "Academic Year not found: 100");
})

```
