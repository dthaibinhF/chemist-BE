```toml
name = 'Delete single grade'
method = 'DELETE'
url = '{{baseUrl}}/grade/:id'
sortWeight = 4000000
id = '2dca507b-b772-47d8-abed-c9004ed649ce'

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
