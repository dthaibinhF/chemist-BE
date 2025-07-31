```toml
name = 'AFTER PUT: single grade'
method = 'GET'
url = '{{baseUrl}}/grade/:id'
sortWeight = 3000000
id = '2bb87c3a-25f7-43a6-8782-66b105185f77'

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

jc.test("Grade has changed", function () {
    jc.expect(jc.response.json('name'), "Updated Test Grade");
})
```
