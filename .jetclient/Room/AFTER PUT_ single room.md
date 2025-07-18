```toml
name = 'AFTER PUT: single room'
method = 'GET'
url = '{{baseUrl}}/room/:id'
sortWeight = 3000000
id = 'a9854d6f-6468-4931-917b-e3e6237a7a6e'

[[pathVariables]]
key = 'id'
value = '{{roomId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Room has changed", function () {
    jc.expect(jc.response.json('name'), "Updated Test Room 102");
    jc.expect(jc.response.json('location'), "Building B");
    jc.expect(jc.response.json('capacity'), 40);
})
```
