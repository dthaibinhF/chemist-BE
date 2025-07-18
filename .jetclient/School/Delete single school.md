```toml
name = 'Delete single school'
method = 'DELETE'
url = '{{baseUrl}}/school/:id'
sortWeight = 4000000
id = '774a8094-dab3-4713-9851-ca315e8ddabb'

[[pathVariables]]
key = 'id'
value = '{{schoolId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 204", function () {
    jc.expect(jc.response.status, 204);
})
```