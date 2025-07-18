```toml
name = 'Login'
method = 'POST'
url = '{{baseUrl}}/auth/login'
sortWeight = 1000000
id = '4a32f0dc-16a3-47a6-b804-5fad1b86566d'

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
