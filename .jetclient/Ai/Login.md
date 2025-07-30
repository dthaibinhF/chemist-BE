```toml
name = 'Login'
method = 'POST'
url = 'https://chemist-server-d7158c3732d1.herokuapp.com/api/v1/auth/login'
sortWeight = 1000000
id = 'bc25fcf4-aeec-4716-9cdd-1559cc09f11b'

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
