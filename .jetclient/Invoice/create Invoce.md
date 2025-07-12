```toml
name = 'create Invoce'
method = 'POST'
url = '{{baseUrl}}/payment-detail'
sortWeight = 1000000
id = '6ab9f388-d11b-445f-ba12-27d30e61d8a9'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "fee_id": 1,
  "fee_name": "học phí hè 12 2025-2026",
  "student_id": 6,
  "student_name": "Lê Khả Hân",
  "pay_method": "CASH",
  "amount": 100000,
  "description": "Tiền tài liệu đóng trước",
  "have_discount": 0
}'''
```
