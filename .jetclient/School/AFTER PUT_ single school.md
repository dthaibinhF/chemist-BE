```toml
name = 'AFTER PUT: single school'
method = 'GET'
url = '{{baseUrl}}/school/:id'
sortWeight = 3000000
id = '79854d6f-6468-4931-917b-e3e6237a7a6e'

[[pathVariables]]
key = 'id'
value = '{{schoolId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("School has changed", function () {
    jc.expect(jc.response.json('name'), "Updated Test School");
})
```