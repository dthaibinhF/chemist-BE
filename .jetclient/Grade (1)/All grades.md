```toml
name = 'All grades'
method = 'GET'
url = '{{baseUrl}}/fee'
sortWeight = 2250000
id = '847025c3-c0f2-4995-8595-5f14abfc192f'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
jc.test("Grade exists in response", function () {
    jc.expect(jc.response.text().includes("Test Grade")).to.be.true;
})
```
