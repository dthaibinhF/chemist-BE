```toml
name = 'AFTER PUT: single academic year'
method = 'GET'
url = '{{baseUrl}}/academic-year/:id'
sortWeight = 3000000
id = '69854d6f-6468-4931-917b-e3e6237a7a6e'

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

jc.test("Academic have change", function () {
    jc.expect(jc.response.json('year'), "2027-2028");
})

```
