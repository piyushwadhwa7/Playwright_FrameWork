#!/usr/bin/env bash
#
# Runs the TestNG suite N times and builds an Allure report whose trend chart
# (Status dynamics) is populated with one data point per run.
#
# How it works (Allure 3.x quirks, learned the hard way):
#   1. Each run: `mvn test` produces fresh allure-results, then
#      `allure classic ... --history-path <file>` appends this run to the
#      history file. We use `classic` ONLY to accumulate history — its history
#      records are the ones the awesome report can read back as trend points.
#      (`allure history` writes records the awesome report ignores; generating
#       `awesome` inside the loop against freshly-written results yields a
#       single-point trend — so neither works for accumulation.)
#   2. After the loop, render the viewable report ONCE with `allure awesome`
#      against the last run's (already-processed) results + the full history.
#
# Do NOT use `mvn clean` — wiping target/ breaks Allure's history resolution.
# The classic report itself renders blank in Allure 3.x; that's why the final
# report is `awesome`. In it, the charts are under "Report ▾ → Graphs".
#
# Usage:
#   ./scripts/allure-report.sh [RUNS]     # default RUNS=4
#   ./scripts/allure-report.sh 6 --open   # 6 runs, then open the report
#
set -euo pipefail

RUNS="${1:-4}"
OPEN=""
[[ "${2:-}" == "--open" ]] && OPEN="1"

# Resolve project root (this script lives in scripts/)
cd "$(dirname "$0")/.."

RESULTS="allure-results"
REPORT="allure-report"
HISTORY="allure-history/allure-history.jsonl"
TMP_REPORT="$(mktemp -d)"

command -v allure >/dev/null 2>&1 || { echo "ERROR: allure CLI not found. Install with: brew install allure"; exit 1; }

echo "Building Allure report from $RUNS run(s)..."
rm -rf "$REPORT" "$(dirname "$HISTORY")"
mkdir -p "$(dirname "$HISTORY")"

for i in $(seq 1 "$RUNS"); do
  echo "===== RUN $i / $RUNS ====="
  rm -rf "$RESULTS"
  mvn test -Dheadless=true --no-transfer-progress \
    | grep -E "Tests run: [0-9]+, Fail" | tail -1 || true
  # Accumulate this run into the history file (throwaway report output)
  allure classic "./$RESULTS" --history-path "$HISTORY" -o "$TMP_REPORT" >/dev/null 2>&1
  echo "runs recorded in history: $(wc -l < "$HISTORY" | tr -d ' ')"
done

# Render the final, viewable report ONCE with the full accumulated history
allure awesome "./$RESULTS" --history-path "$HISTORY" -o "$REPORT" >/dev/null
rm -rf "$TMP_REPORT"

TREND=$(python3 -c "import json;d=json.load(open('$REPORT/widgets/charts.json'))['general'];print([len(v['data']) for v in d.values() if v.get('type')=='statusDynamics'][0])" 2>/dev/null || echo "?")

echo ""
echo "Report generated at: $(pwd)/$REPORT"
echo "Status-dynamics trend points: $TREND"
echo "View it with:        allure open $REPORT   (charts under 'Report ▾ → Graphs')"

if [[ -n "$OPEN" ]]; then
  allure open "$REPORT"
fi
