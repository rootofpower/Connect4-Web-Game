# Raw notes: simulated PostgreSQL outage, 2026-10-01

Source data for the runbook and the postmortem. All times are UTC.
Alert times come from the `ALERTS` series in VictoriaMetrics (15 s resolution),
log times from Elasticsearch (`connect4-logs-*`).

## Timeline

| Time (UTC) | Event | Source |
|---|---|---|
| 17:36:32 | `docker compose stop db` | manual note |
| 17:36:32 | First backend ERROR: `SqlExceptionHelper : FATAL: terminating connection due to administrator command` | Elasticsearch |
| 17:36:45 | `pg_up` = 0; PostgresDown → pending | VictoriaMetrics |
| 17:37:45 | PostgresDown → firing (Telegram received shortly after) | VictoriaMetrics, Telegram |
| 17:41:15 | High5xxRatio and HighLatencyP95 → pending | VictoriaMetrics |
| 17:43:15 | High5xxRatio → firing | VictoriaMetrics |
| 17:45:40 | `docker compose start db` | manual note |
| 17:45:45 | PostgresDown resolved | VictoriaMetrics |
| 17:46:00 | `pg_up` = 1 | VictoriaMetrics |
| 17:46:15 | HighLatencyP95 → firing (after recovery) | VictoriaMetrics |
| 17:46:15–17:46:30 | High5xxRatio and HighLatencyP95 resolved | VictoriaMetrics |

## Numbers

- Outage duration: 17:36:32 → 17:45:40 ≈ 9 min 8 s
- Time to detect (stop → PostgresDown firing): 73 s
- Backend ERROR log lines during the outage: 58
- BackendDown: never fired

## Observations

- BackendDown did not fire: `/actuator/prometheus` does not need the DB, so `up{job="backend"}` stayed 1.
- `/actuator/health/readiness` returned `UP` during the outage (readiness group does not include `db` by default).
- Request rate dropped to ~0: requests waited for a DB connection (HikariCP timeout, 30 s by default) before failing.
- DB container status was `Exited (0)`: graceful stop, not a crash.
- High5xxRatio fired 5 min 43 s after the start (`for: 2m` + 5 min rate window).
- HighLatencyP95 fired at 17:46:15, after the DB was already back: the 5 min window lags behind reality,
  so this alert was noise. Candidate action item: shorter window or drop the alert while PostgresDown is firing
  (Alertmanager inhibit rule).
