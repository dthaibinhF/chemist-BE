```toml
name = 'AFTER DELETE: single room'
method = 'GET'
url = '{{baseUrl}}/room/:id'
sortWeight = 5000000
id = '7c4c351a-557f-481f-a0cb-0d6598b22faa'

[[pathVariables]]
key = 'id'
value = '{{roomId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("FAILED TEST: status code is 404", function () {
    jc.expect(jc.response.status, 404);
})

jc.test("FAILED TEST: Room not Exist", function () {
    jc.expect(jc.response.json('error')).to.include("Room not found");
})
```
