```toml
name = 'get by student id'
method = 'GET'
url = '{{baseUrl}}/payment-detail/student/:id'
sortWeight = 2000000
id = '95b89efd-4d0d-44a5-9490-c72d4bbaaa9c'

[[pathVariables]]
key = 'id'
value = '6'

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
