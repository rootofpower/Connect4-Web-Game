# Runbook: PostgresDown

## Alert

`pg_up == 0` for 1 minute. Severity: critical.
Source: `observability/vmalert/rules/connect4.yml`, sent to Telegram through Alertmanager.

## What it means

postgres-exporter cannot connect to PostgreSQL. The database is stopped, crashed or unreachable.

## Impact

The backend cannot get a database connection. Every request that touches the DB waits
up to 30 s (HikariCP default connection timeout) and then fails with a 5xx error.
Users cannot load the leaderboard, log in or play.

Expect these alerts to follow: `High5xxRatio` after ~5–6 min, and possibly `HighLatencyP95`.
`BackendDown` will **not** fire: the backend process is alive and still exposes metrics.

## Diagnosis

1. Check the container state:

   ```bash
   docker compose ps -a db
   ```

   - `Up`: the container runs, so the problem is inside PostgreSQL or the network. Go to step 2.
   - `Exited (0)`: stopped on purpose (`docker stop` or `docker compose stop`).
   - `Exited (137)`: killed, often out of memory. Check `docker inspect gamestudio_db --format '{{.State.OOMKilled}}'`.
   - Any other non-zero code: PostgreSQL crashed. Go to step 2.

2. Read the database logs:

   ```bash
   docker compose logs --tail 50 db
   ```

3. Confirm the impact on the backend in Grafana → Explore → Elasticsearch (Lucene):

   ```
   service:gamestudio_backend AND level:ERROR
   ```

   Typical messages: `The connection attempt failed`, `terminating connection due to administrator command`.

4. Check the "Connect4 - RED" dashboard: request rate drops, 5xx ratio rises, `PostgreSQL up` shows 0.

Do not rely on `/actuator/health/readiness`: it stays `UP` because the readiness group does not check the DB.

## Mitigation

```bash
docker compose start db
```

If the container does not start, read `docker compose logs db` and check free disk space with `df -h`.

## Verification

- `docker compose ps db` shows `Up`.
- `curl -s -o /dev/null -w '%{http_code}\n' 'localhost:8080/api/connect4/leaderboard?game=connect4'` returns `200`.
- `PostgreSQL up` on the dashboard is back to 1.
- Telegram shows `RESOLVED` for PostgresDown.

The backend reconnects on its own (HikariCP); no restart is needed.

## Escalation

If PostgreSQL does not start or the data volume looks damaged, do not delete the volume.
Escalate to the database owner.
