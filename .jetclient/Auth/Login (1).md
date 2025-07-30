```toml
name = 'Login (1)'
method = 'POST'
url = '{{baseUrl}}/auth/login'
sortWeight = 3000000
id = '8ab06de3-2f27-4d57-a554-ce06dd3f36ed'

[body]
type = 'JSON'
raw = '''
{
"email": "{{dev.email}}",
"password": "{{dev.password}}"
}'''
```

#### Post-response Script

```js
jc.globals.set("access_token","Bearer " + jc.response.json("access_token"));
console.log(jc.variables.get("access_token"));

jc.test("status code is 200", function () {
    jc.expect(jc.response.status, 200);
})

```
