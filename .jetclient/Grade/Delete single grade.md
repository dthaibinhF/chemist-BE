```toml
name = 'Delete single grade'
method = 'DELETE'
url = '{{baseUrl}}/grade/:id'
sortWeight = 4000000
id = '874a8094-dab3-4713-9851-ca315e8ddabb'

[[pathVariables]]
key = 'id'
value = '{{gradeId}}'

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