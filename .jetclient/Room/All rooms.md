```toml
name = 'All rooms'
method = 'GET'
url = '{{baseUrl}}/room'
sortWeight = 2250000
id = '868286b2-8423-4c8f-aaf2-63a1a26328d1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
jc.test("Room exists in response", function () {
    jc.expect(jc.response.text().includes("Test Room 101")).to.be.true;
})
```
