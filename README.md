# basic-app-service
basic-app-service 
# aws-lab — Java + MySQL + REST + CPU burn

Tiny Spring Boot 3 app (Java 17) for practising AWS deployments.
Config is 100% env vars: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `PORT`.

## Endpoints
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/items` `{"name":"x"}` | Insert row in MySQL (records which host served it) |
| GET | `/api/items`, `/api/items/{id}` | Read rows |
| DELETE | `/api/items/{id}` | Delete row |
| GET | `/api/cpu/burn?seconds=20&threads=2` | Burn CPU (prime counting), returns host + stats |
| GET | `/api/info` | Host/pod, cores, memory, Lambda/K8s env |
| GET | `/actuator/health` | Health + `/readiness`, `/liveness` probes |

## Local
```
docker compose up --build
curl localhost:8080/api/info
curl -X POST localhost:8080/api/items -H 'Content-Type: application/json' -d '{"name":"hello"}'
curl "localhost:8080/api/cpu/burn?seconds=10&threads=2"
```

## EC2
1. RDS MySQL (or MySQL on a 2nd EC2); SG: allow 3306 from app SG only.
2. EC2 (Amazon Linux 2023) + `sudo dnf install -y java-17-amazon-corretto`; SG: allow 8080.
3. `mvn package` locally → `scp target/app.jar`.
4. `DB_URL=jdbc:mysql://<rds>:3306/labdb DB_USER=admin DB_PASSWORD=... java -jar app.jar`
5. Try: systemd unit, ALB + target group + ASG, CloudWatch CPU alarm, then `burn` to trigger scale-out.

## ECS/Fargate (optional)
Push `Dockerfile` image to ECR → task def (cpu/memory) → service behind ALB; DB creds from Secrets Manager.

## EKS
```
docker build -t aws-lab:1.0 . && docker tag/push to ECR
eksctl create cluster --name lab --nodes 2     # or Terraform
# edit image + DB_URL in k8s/app.yaml
kubectl apply -f k8s/app.yaml
kubectl get hpa -w
# generate load across pods:
for i in $(seq 1 20); do curl -s "http://<LB>/api/cpu/burn?seconds=60&threads=2" & done
```
Needs metrics-server for HPA. RDS SG must allow node/pod SG.

## Lambda (container image + Lambda Web Adapter, no code change)
```
docker build -f Dockerfile.lambda -t aws-lab-lambda .   # push to ECR
```
- Create function from the image; memory ≥ 1024 MB (CPU scales with memory — compare burn results at 512 vs 2048 MB).
- Timeout 30–60 s; attach to VPC (same as RDS); use RDS Proxy to avoid connection exhaustion.
- Add a Function URL or API Gateway HTTP API.
- Observe cold start vs warm in CloudWatch Logs (`Init Duration`).
- Keep `burn` seconds under the timeout.

## Learning ideas
Cold start vs warm · CPU limits throttling in K8s · HPA vs ASG scaling · RDS Proxy · Secrets Manager/IAM roles (IRSA) · CloudWatch/Container Insights · ALB health checks · cost per approach.
