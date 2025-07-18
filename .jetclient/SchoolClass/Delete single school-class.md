```toml
name = 'Delete single school-class'
method = 'DELETE'
url = '{{baseUrl}}/school-class/:id'
sortWeight = 4000000
id = '974a8094-dab3-4713-9851-ca315e8ddabb'

[[pathVariables]]
key = 'id'
value = '{{schoolClassId}}'

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