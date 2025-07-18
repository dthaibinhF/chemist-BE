```toml
name = 'All school-classes'
method = 'GET'
url = '{{baseUrl}}/school-class'
sortWeight = 2250000
id = '768286b2-8423-4c8f-aaf2-63a1a26328d1'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})
jc.test("School Class exists in response", function () {
    jc.expect(jc.response.text().includes("Test Class 10A")).to.be.true;
})
```