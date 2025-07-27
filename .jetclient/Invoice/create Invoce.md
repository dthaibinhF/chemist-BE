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
  "student_id": 114,
  "student_name": "Trần Huy Đạt",
  "pay_method": "BANK_TRANSFER",
  "amount": 1500000,
  "description": "",
  "have_discount": 0,
  "payment_status": "PAID",
  "due_date": "2025-07-27T00:00:00.000Z",
  "generated_amount": 1500000,
  "is_overdue": true,
  "academicYearId": 4,
  "groupId": 3
}'''
```
