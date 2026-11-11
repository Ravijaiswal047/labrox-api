import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },   // Ramp-up to 50 users
    { duration: '1m', target: 200 },    // Peak load: 200 concurrent users
    { duration: '30s', target: 0 },     // Ramp-down
  ],
  thresholds: {
    // Requirements: list/search p95 < 400ms, record read p95 < 200ms
    'http_req_duration{type:search}': ['p(95)<400'],
    'http_req_duration{type:read}': ['p(95)<200'],
    'http_req_failed': ['rate<0.01'], // < 1% errors
  },
};

const BASE_URL = __ENV.GATEWAY_URL || 'http://localhost:8088';

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  // 1. Health & Ping
  let res = http.get(`${BASE_URL}/actuator/health`, { tags: { type: 'read' } });
  check(res, { 'status is 200': (r) => r.status === 200 });

  // 2. Authentication Step 1
  let otpPayload = JSON.stringify({ UserName: 'admin' });
  res = http.post(`${BASE_URL}/api/v1/Login/LoginWithOTP`, otpPayload, {
    headers: headers,
    tags: { type: 'read' },
  });
  check(res, { 'otp response 200': (r) => r.status === 200 });

  // 3. Authentication Step 2 (Token Issuance)
  let loginPayload = JSON.stringify({ UserName: 'admin', Password: 'Admin@123' });
  res = http.post(`${BASE_URL}/api/v1/Login/login`, loginPayload, {
    headers: headers,
    tags: { type: 'read' },
  });
  check(res, { 'login response 200': (r) => r.status === 200 });

  let token = '';
  try {
    let body = JSON.parse(res.body);
    if (body.message && body.message.token) {
      token = body.message.token;
    }
  } catch (e) {}

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // 4. Heavy Search: Sample Collection Worklist
  let scPayload = JSON.stringify({
    CentreID: 1,
    FromDate: '01-Jan-2026',
    ToDate: '31-Dec-2026',
  });
  res = http.post(`${BASE_URL}/api/v1/SC/GetSampleCollection`, scPayload, {
    headers: headers,
    tags: { type: 'search' },
  });
  check(res, { 'sample search 200': (r) => r.status === 200 });

  // 5. Heavy Search: Diagnostic Worklist
  let rePayload = JSON.stringify({ CentreID: 1, DepartmentID: 1 });
  res = http.post(`${BASE_URL}/api/v1/RE/GetResultEntry`, rePayload, {
    headers: headers,
    tags: { type: 'search' },
  });
  check(res, { 'diagnostic search 200': (r) => r.status === 200 });

  // 6. Doctor Approval Patient Search
  res = http.post(`${BASE_URL}/api/v1/DoctorApproval/PatientSearch`, JSON.stringify({ CentreID: 1 }), {
    headers: headers,
    tags: { type: 'search' },
  });
  check(res, { 'approval search 200': (r) => r.status === 200 });

  sleep(1);
}
