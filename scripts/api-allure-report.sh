#!/usr/bin/env bash
#
# Runs the API-only TestNG suite N times and builds an Allure report with
# cross-run API trend history.
#
# Usage:
#   ./scripts/api-allure-report.sh [RUNS]     # default RUNS=4
#   ./scripts/api-allure-report.sh 6 --open   # 6 runs, then open the report
#
set -euo pipefail

RUNS="${1:-4}"
OPEN=""
[[ "${2:-}" == "--open" ]] && OPEN="1"

# Resolve project root (this script lives in scripts/)
cd "$(dirname "$0")/.."

RESULTS="allure-results"
REPORT="allure-api-report"
HISTORY="allure-history/api-allure-history.jsonl"
API_SUITE="src/test/resources/TestRunners/testng_api.xml"
TMP_REPORT="$(mktemp -d)"

command -v allure >/dev/null 2>&1 || { echo "ERROR: allure CLI not found. Install with: brew install allure"; exit 1; }
command -v node >/dev/null 2>&1 || { echo "ERROR: node not found. This Allure CLI install requires Node.js on PATH. Install with: brew install node"; exit 1; }

echo "Building API Allure report from $RUNS run(s)..."
rm -rf "$REPORT" "$HISTORY"
mkdir -p "$(dirname "$HISTORY")"

for i in $(seq 1 "$RUNS"); do
  echo "===== API RUN $i / $RUNS ====="
  rm -rf "$RESULTS"
  mvn test -Dsurefire.suiteXmlFiles="$API_SUITE" -Dheadless=true --no-transfer-progress \
    | grep -E "Tests run: [0-9]+, Fail" | tail -1 || true
  allure classic "./$RESULTS" --history-path "$HISTORY" -o "$TMP_REPORT" >/dev/null 2>&1
  echo "API runs recorded in history: $(wc -l < "$HISTORY" | tr -d ' ')"
done

allure awesome "./$RESULTS" --history-path "$HISTORY" -o "$REPORT" >/dev/null
rm -rf "$TMP_REPORT"

TREND=$(python3 -c "import json;d=json.load(open('$REPORT/widgets/charts.json'))['general'];print([len(v['data']) for v in d.values() if v.get('type')=='statusDynamics'][0])" 2>/dev/null || echo "?")

echo ""
echo "API report generated at: $(pwd)/$REPORT"
echo "API status-dynamics trend points: $TREND"
echo "View it with:        allure open $REPORT   (charts under 'Report ▾ → Graphs')"

if [[ -n "$OPEN" ]]; then
  allure open "$REPORT"
fi
