```toml
name = 'Delete single academic year'
method = 'DELETE'
url = '{{baseUrl}}/academic-year/:id'
sortWeight = 4000000
id = '674a8094-dab3-4713-9851-ca315e8ddabb'

[[pathVariables]]
key = 'id'
value = '{{academicYearId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 201", function () {
    jc.expect(jc.response.status, 201);
})

```
