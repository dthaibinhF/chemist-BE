```toml
name = 'All academic year'
method = 'GET'
url = '{{baseUrl}}/academic-year'
sortWeight = 2250000
id = '468286b2-8423-4c8f-aaf2-63a1a26328d1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
jc.test("Academic have change", function () {
    jc.expect(jc.response.text().includes( "2026-2027")).to.be.true;
})
```
