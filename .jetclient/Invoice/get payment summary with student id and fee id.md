```toml
name = 'get payment summary with student id and fee id'
method = 'GET'
url = '{{baseUrl}}/payment-detail/student/:id/fee/:feeid'
sortWeight = 5000000
id = 'd3f21faa-2936-4ac5-a731-5c00baa74ea6'

[[pathVariables]]
key = 'id'
value = '362'

[[pathVariables]]
key = 'feeid'
value = '5'

[[headers]]
key = 'Authorization'
value = '{{access_token}}'

[body]
type = 'JSON'
raw = '''
{
  "fee_id": 1,
  "student_id": 114,
  "pay_method": "BANK_TRANSFER",
  "amount": 1500000,
  "description": "Tiền tài liệu đóng trước",
  "have_discount": 0,
  "payment_status": "PENDING",
  "due_date": '2025-07-27T00:15:30.000+07:00',
  "generated_amount": 1500000,
  "effective_discount": 0,
  "is_overdue": false
}'''
```
