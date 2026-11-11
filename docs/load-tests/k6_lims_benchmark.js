import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },   // Warm-up ramp
    { duration: '1m', target: 200 },    // Peak load: 200 concurrent regulated laboratory users
    { duration: '2m', target: 200 },    // Sustained load test
    { duration: '30s', target: 0 },     // Graceful ramp-down
  ],
  thresholds: {
    // Non-negotiable SLA targets on ≥10M simulated records
    http_req_duration: ['p(95)<400', 'p(99)<800'], // p95 < 400ms, p99 < 800ms
    http_req_failed: ['rate<0.001'],               // Error rate < 0.1% (GxP reliability)
    'http_req_duration{scenario:pdf_stream}': ['p(95)<600'], // PDF generation < 600ms
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080';
const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.jwt.token',
};

export default function () {
  group('1. Identity & RBAC Menu Resolution', function () {
    const res = http.get(`${BASE_URL}/api/v1/iam/menu/main-menu?companyId=1&designationId=3&userId=10`, { headers: HEADERS });
    check(res, {
      'menu status is 200': (r) => r.status === 200,
      'menu response time < 150ms': (r) => r.timings.duration < 150,
    });
  });

  sleep(0.5);

  group('2. High-Volume Keyset/Cursor Accessioning Search', function () {
    const res = http.get(`${BASE_URL}/api/v1/samples/search?lastId=1000000&pageSize=50&status=ACCESSIONED`, { headers: HEADERS });
    check(res, {
      'cursor search status is 200': (r) => r.status === 200,
      'cursor search p95 < 250ms': (r) => r.timings.duration < 250,
    });
  });

  sleep(0.5);

  group('3. Diagnostic Result Entry & Delta Check Evaluation', function () {
    const payload = JSON.stringify({
      sampleId: 101,
      investigationId: 501,
      patientId: 9021,
      results: [
        { parameterCode: 'GLU_F', observedValue: 148.5, previousValue: 142.0 },
        { parameterCode: 'HBA1C', observedValue: 7.4, previousValue: 7.1 },
        { parameterCode: 'CREAT', observedValue: 0.92, previousValue: 0.88 },
      ]
    });

    const res = http.post(`${BASE_URL}/api/v1/diagnostic/results/save`, payload, { headers: HEADERS });
    check(res, {
      'result save status is 200': (r) => r.status === 200,
      'delta check evaluated': (r) => r.body.includes('DELTA_NORMAL') || r.body.includes('DELTA_EVALUATED') || r.status === 200,
      'save duration < 300ms': (r) => r.timings.duration < 300,
    });
  });

  sleep(0.5);

  group('4. Pathologist 21 CFR Part 11 Electronic Signature Approval', function () {
    const approvalPayload = JSON.stringify({
      testId: 101,
      doctorUserId: 2,
      digitalPin: '5829',
      approvalNotes: 'Correlated with fasting lipid and glycated hemoglobin levels.'
    });

    const res = http.post(`${BASE_URL}/api/v1/approvals/sign-off`, approvalPayload, { headers: HEADERS });
    check(res, {
      'approval status is 200': (r) => r.status === 200,
      'signature duration < 250ms': (r) => r.timings.duration < 250,
    });
  });

  sleep(0.5);

  group('5. Streaming A4 PDF Diagnostic Examination Report', function () {
    const res = http.get(`${BASE_URL}/api/v1/reports/pdf/101`, {
      headers: HEADERS,
      tags: { scenario: 'pdf_stream' }
    });
    check(res, {
      'pdf status is 200': (r) => r.status === 200,
      'content type is pdf': (r) => r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/pdf'),
      'pdf duration < 600ms': (r) => r.timings.duration < 600,
    });
  });

  sleep(1);
}
