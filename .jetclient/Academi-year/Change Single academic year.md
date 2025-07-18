```toml
name = 'Change Single academic year'
method = 'PUT'
url = '{{baseUrl}}/academic-year/:id'
sortWeight = 2500000
id = '867de07b-e508-4862-910f-5edd048a7f8b'

[[pathVariables]]
key = 'id'
value = '{{academicYearId}}'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "year": "2027-2028",
}'''
```

#### Post-response Script

```js
jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

jc.test("Academic change success", function () {
        jc.expect(jc.response.json('year'), "2027-2028");
})


```
