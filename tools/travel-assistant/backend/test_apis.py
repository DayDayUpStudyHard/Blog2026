"""Test each API independently to find which one fails."""
import os, sys, json, time

# Add backend to path
sys.path.insert(0, os.path.dirname(__file__))

from dotenv import load_dotenv
load_dotenv()

AMAP_KEY = os.getenv("AMAP_API_KEY", "")
LLM_KEY = os.getenv("LLM_API_KEY", "")
LLM_BASE = os.getenv("LLM_BASE_URL", "https://api.openai.com/v1")
LLM_MODEL = os.getenv("LLM_MODEL", "gpt-3.5-turbo")
UNSPLASH_KEY = os.getenv("UNSPLASH_ACCESS_KEY", "")

CITY = "杭州"
results = {}

def hdr(title):
    print(f"\n{'='*50}")
    print(f"  {title}")
    print(f"{'='*50}")

# ── Test 1: Amap POI Search ──
hdr("Test 1: Amap POI Search")
try:
    import requests
    resp = requests.get("https://restapi.amap.com/v3/place/text", params={
        "key": AMAP_KEY, "keywords": "旅游 景点", "city": CITY, "offset": 3, "output": "json"
    }, timeout=10)
    data = resp.json()
    print(f"  status: {data.get('status')}, info: {data.get('info')}")
    pois = data.get("pois", [])
    for p in pois[:3]:
        print(f"  - {p.get('name')} ({p.get('address')})")
    results["amap_poi"] = "OK" if data.get("status") == "1" else f"FAIL: {data.get('info')}"
except Exception as e:
    results["amap_poi"] = f"EXCEPTION: {e}"
    print(f"  ERROR: {e}")

# ── Test 2: Amap Weather ──
hdr("Test 2: Amap Weather")
try:
    resp = requests.get("https://restapi.amap.com/v3/weather/weatherInfo", params={
        "key": AMAP_KEY, "city": CITY, "extensions": "all", "output": "json"
    }, timeout=10)
    data = resp.json()
    print(f"  status: {data.get('status')}, info: {data.get('info')}")
    forecasts = data.get("forecasts", [])
    if forecasts:
        print(f"  city: {forecasts[0].get('city')}, province: {forecasts[0].get('province')}")
        casts = forecasts[0].get("casts", [])
        for c in casts[:3]:
            print(f"  - {c.get('date')}  {c.get('dayweather')}/{c.get('nightweather')}  {c.get('daytemp')}°C~{c.get('nighttemp')}°C")
    results["amap_weather"] = "OK" if data.get("status") == "1" else f"FAIL: {data.get('info')}"
except Exception as e:
    results["amap_weather"] = f"EXCEPTION: {e}"
    print(f"  ERROR: {e}")

# ── Test 3: LLM API (DeepSeek) ──
hdr("Test 3: LLM API (DeepSeek)")
try:
    from openai import OpenAI
    client = OpenAI(api_key=LLM_KEY, base_url=LLM_BASE)
    t0 = time.time()
    resp = client.chat.completions.create(
        model=LLM_MODEL,
        messages=[{"role": "user", "content": "回复两个字：你好"}],
        temperature=0.1,
        max_tokens=20,
        timeout=30,
    )
    elapsed = time.time() - t0
    text = resp.choices[0].message.content
    print(f"  response: {text}")
    print(f"  model: {resp.model}, time: {elapsed:.1f}s, tokens: {resp.usage.total_tokens if resp.usage else 'N/A'}")
    results["llm"] = f"OK ({elapsed:.1f}s)"
except Exception as e:
    results["llm"] = f"EXCEPTION: {e}"
    print(f"  ERROR: {e}")

# ── Test 4: Unsplash (optional) ──
hdr("Test 4: Unsplash (optional)")
if UNSPLASH_KEY and UNSPLASH_KEY != "your_unsplash_access_key_here":
    try:
        resp = requests.get("https://api.unsplash.com/search/photos", params={
            "query": "Hangzhou West Lake", "per_page": 1
        }, headers={"Authorization": f"Client-ID {UNSPLASH_KEY}"}, timeout=10)
        data = resp.json()
        print(f"  status: {resp.status_code}")
        results_list = data.get("results", [])
        if results_list:
            print(f"  image: {results_list[0].get('urls', {}).get('small', 'N/A')}")
        results["unsplash"] = "OK" if results_list else "OK (no results)"
    except Exception as e:
        results["unsplash"] = f"EXCEPTION: {e}"
        print(f"  ERROR: {e}")
else:
    print("  SKIP: Unsplash key not configured")
    results["unsplash"] = "SKIP"

# ── Summary ──
hdr("SUMMARY")
all_ok = True
for name, result in results.items():
    status = "PASS" if result.startswith("OK") else ("SKIP" if result == "SKIP" else "FAIL")
    if status == "FAIL":
        all_ok = False
    print(f"  {name:15s}  [{status}]  {result}")

if not all_ok:
    print("\n  ⚠ 请根据上面的 FAIL 项检查对应的 API 配置。")
    if results.get("llm", "").startswith("EXCEPTION"):
        print("  → LLM 可能是 API Key 无效 或 base_url 无法访问")
    if results.get("amap_poi", "").startswith("FAIL"):
        print("  → 高德地图 Key 可能无效 或 未开通 Web服务 API")
else:
    print("\n  所有 API 均可用，请运行后端并通过前端提交旅行计划进行完整测试。")
