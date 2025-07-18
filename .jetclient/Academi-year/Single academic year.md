```toml
name = 'Single academic year'
method = 'GET'
url = '{{baseUrl}}/academic-year/:id'
sortWeight = 2000000
id = 'a12377f3-cbcb-4d37-a004-c88fb9dc3a0b'

[[pathVariables]]
key = 'id'
value = '{{academicYearId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Academic Exist", function () {
        jc.expect(jc.response.json('id'), jc.variables.get("academicYearId"));
})

```
