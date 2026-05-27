"""Test the full trip/plan endpoint directly."""
import requests, json

url = "http://localhost:8001/api/trip/plan"
payload = {
    "city": "杭州",
    "start_date": "2026-06-01",
    "end_date": "2026-06-01",
    "days": 1,
    "preferences": "自然风光",
    "budget": "经济",
    "transportation": "公共交通",
    "accommodation": "经济型"
}

print(f"POST {url}")
print(f"Payload: {json.dumps(payload, ensure_ascii=False)}")
print("-" * 60)

try:
    resp = requests.post(url, json=payload, timeout=300)
    print(f"Status: {resp.status_code}")
    if resp.status_code == 200:
        data = resp.json()
        print(f"Success! City={data.get('city')}, Days={len(data.get('days', []))}")
        if data.get('days'):
            for d in data['days']:
                print(f"  Day {d.get('day_index')}: {len(d.get('attractions', []))} attractions")
    else:
        print(f"Error: {resp.text}")
except requests.exceptions.ConnectionError:
    print("FAIL: Cannot connect to localhost:8001 - is the backend running?")
except Exception as e:
    print(f"FAIL: {e}")
