```toml
name = 'Single school-class'
method = 'GET'
url = '{{baseUrl}}/school-class/:id'
sortWeight = 2000000
id = 'd12377f3-cbcb-4d37-a004-c88fb9dc3a0b'

[[pathVariables]]
key = 'id'
value = '{{schoolClassId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.response.to.have.status(200)
})

jc.test("School Class Exists", function () {
    jc.expect(jc.response.json('id'), jc.variables.get("schoolClassId"));
})
```
