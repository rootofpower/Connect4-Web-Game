# Failed rollout: frontend with non-existent image tag (practice), 2026-10-06

## What happened

Helm revision 3 deployed the frontend with the image tag `broken`, which does not exist in ghcr.io.

## Detection

- `kubectl get pods`: the new frontend pod stayed in `ErrImagePull`.
- `kubectl describe pod`: `Failed to pull image "ghcr.io/rootofpower/connect4-frontend:broken": ... NotFound`.

## Impact

None. The game stayed available during the whole rollout.

## Why users were not affected

Because of only 1 replica, maxSurge ceil to 1, maxUnavailable ceil to 0, so, firstly, k3s run a new pod, and wait it to
became `READY`, but new broken pod got stuck in `ErrImagePull`, so old continue to work.

## Resolution

- `helm rollback c4 2` -> revision 4, the broken pod was removed.
- A second `helm rollback c4` without a revision number went back to broken revision 3 (revision 5); fixed with
  `helm rollback c4 1` (revision 6).

## Lessons

- Always pass the revision number to `helm rollback` after checking `helm history`.
- A hardcoded image in a template ignores `values.yaml` and `--set`; check the result with `helm template` before
  deploying.
- `helm upgrade --atomic` rolls back automatically, if the release does not become ready.
