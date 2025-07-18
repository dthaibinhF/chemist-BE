```toml
name = 'All schools'
method = 'GET'
url = '{{baseUrl}}/school'
sortWeight = 2250000
id = '568286b2-8423-4c8f-aaf2-63a1a26328d1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
jc.test("School exists in response", function () {
    jc.expect(jc.response.text().includes("Test School")).to.be.true;
})
```