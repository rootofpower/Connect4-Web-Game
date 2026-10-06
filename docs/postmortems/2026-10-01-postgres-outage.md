# Postmortem: PostgreSQL outage (simulated), 2026-10-01

Blameless format. All times are UTC. Raw data: `docs/incident-notes-2026-10-01.md`.

## Summary

To test monitoring and alerting, the PostgreSQL container was stopped on purpose.
For about 9 minutes the backend could not serve any request that needs the database.
The `PostgresDown` alert reached Telegram 73 seconds after the outage started.

## Impact

- Duration: 17:36:32 → 17:45:40, about 9 min.
- Requests waited for a DB connection for up to 30 s and then failed with 5xx.
- Request rate dropped to about 0, because almost no request completed.
- 58 backend ERROR log lines.

## Timeline (UTC)

| Time     | Event                                                                                                                       |
|----------|-----------------------------------------------------------------------------------------------------------------------------|
| 17:36:32 | DB container stopped (`docker compose stop db`). First backend ERROR: `terminating connection due to administrator command` |
| 17:36:45 | `pg_up` = 0, `PostgresDown` pending                                                                                         |
| 17:37:45 | `PostgresDown` firing; notification received in Telegram                                                                    |
| 17:41:15 | `High5xxRatio` and `HighLatencyP95` pending                                                                                 |
| 17:43:15 | `High5xxRatio` firing                                                                                                       |
| 17:45:40 | DB container started (`docker compose start db`)                                                                            |
| 17:45:45 | `PostgresDown` resolved                                                                                                     |
| 17:46:00 | `pg_up` = 1                                                                                                                 |
| 17:46:15 | `HighLatencyP95` firing, after the DB was already back                                                                      |
| 17:46:30 | `High5xxRatio` and `HighLatencyP95` resolved                                                                                |

## Detection

- `PostgresDown` fired 73 s after the stop (`for: 1m` plus the 15 s scrape and evaluation interval).
- `High5xxRatio` fired after 5 min 43 s: it needs `for: 2m` on top of a 5-minute rate window.
- `BackendDown` never fired. It only checks `up{job="backend"}`, and `/actuator/prometheus`
  does not need the DB, so the backend still looked healthy.

## Root cause

The PostgreSQL container was stopped (simulated failure). The backend has no fallback
when the database is unavailable.

## Resolution

The DB container was started again. HikariCP reconnected on its own; the backend did not need a restart.

## What went well

- Symptom-based alerts caught the outage: `PostgresDown` first, then `High5xxRatio`.
- Logs in Elasticsearch showed the cause right away (`SqlExceptionHelper` errors).
- Recovery took one command and no data was lost.

## What went wrong

- Users waited up to 30 s for each failed request (default HikariCP connection timeout).
- `/actuator/health/readiness` stayed `UP`. In Kubernetes the pod would keep receiving traffic.
- `HighLatencyP95` fired after recovery: its 5-minute window lags behind reality, so the alert was noise.
- `BackendDown` only proves the process is alive, not that the service works.

## Action items

- [X] Lower the HikariCP connection timeout (`spring.datasource.hikari.connection-timeout`) so requests fail fast.
- [X] Add `db` to the readiness group: `management.endpoint.health.group.readiness.include=readinessState,db`.
  Do not add it to liveness, or Kubernetes would restart healthy pods in a loop.
- [ ] Add an Alertmanager inhibit rule: mute `High5xxRatio` and `HighLatencyP95` while `PostgresDown` is firing.
- [ ] Link this runbook from the `PostgresDown` alert annotation (`runbook_url`).

## Follow-up (2026-10-06, Kubernetes)

Both fixes were applied as environment variables in the Helm chart and verified by scaling
PostgreSQL to 0 replicas:

- About 13 s after the database stopped, the backend pod went to `0/1` Ready (readiness `periodSeconds: 5` × default
  `failureThreshold: 3` ≈ 15 s) and stopped receiving traffic.
- The pod was not restarted (`RESTARTS` stayed the same): liveness does not check the DB.
- `/actuator/health/readiness` returned `{"status":"DOWN"}` with HTTP 503.
- After PostgreSQL was scaled back to 1, the backend became Ready again 4s after database, with no restart.
