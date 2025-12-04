# Redis StatefulSet 배포 가이드

## 주요 개선 사항

### 1. Persistence 설정
- **AOF (Append-Only File)** 사용으로 데이터 안정성 향상
- `appendfsync everysec`: 매초마다 디스크에 동기화 (성능과 안정성의 균형)

### 2. Health Checks
- **Liveness Probe**: TCP 소켓으로 Redis 프로세스 확인
- **Readiness Probe**: `redis-cli ping`으로 서비스 준비 상태 확인

### 3. Resource Management
- **Requests**: CPU 100m, Memory 256Mi (최소 보장)
- **Limits**: CPU 500m, Memory 512Mi (최대 제한)

### 4. Configuration Management
- ConfigMap을 통한 중앙화된 설정 관리
- Redis 설정 파일로 세밀한 튜닝 가능

## StorageClass 확인 및 수정

### 현재 클러스터의 StorageClass 확인
```bash
kubectl get storageclass
```

### gp2가 없는 경우 대체 방법

#### 옵션 1: 기본 StorageClass 사용
statefulset.yaml에서 `storageClassName` 필드를 제거하거나 빈 문자열로 설정:
```yaml
storageClassName: ""  # 기본 StorageClass 사용
```

#### 옵션 2: 다른 StorageClass 사용
클러스터에서 사용 가능한 StorageClass로 변경:
```yaml
# GCP의 경우
storageClassName: standard

# Azure의 경우
storageClassName: default

# On-premise (NFS 등)
storageClassName: nfs-client
```

#### 옵션 3: gp2 StorageClass 생성 (AWS EKS 전용)
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: gp2
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp2
  fsType: ext4
volumeBindingMode: WaitForFirstConsumer
allowVolumeExpansion: true
```

## 배포 방법

### Kustomize를 사용한 배포
```bash
# dry-run으로 생성될 리소스 확인
kubectl kustomize k8s/redis/base

# 배포 실행
kubectl apply -k k8s/redis/base

# 상태 확인
kubectl get statefulset redis -n goormthon-6
kubectl get pods -l app=redis -n goormthon-6
kubectl get pvc -l app=redis -n goormthon-6
```

### 직접 배포
```bash
kubectl apply -f k8s/redis/base/configmap.yaml -n goormthon-6
kubectl apply -f k8s/redis/base/service.yaml -n goormthon-6
kubectl apply -f k8s/redis/base/statefulset.yaml -n goormthon-6
```

## 문제 해결

### Pod가 Pending 상태인 경우
```bash
# Pod 상태 확인
kubectl describe pod redis-0 -n goormthon-6

# PVC 상태 확인
kubectl get pvc -n goormthon-6
kubectl describe pvc data-redis-0 -n goormthon-6
```

**일반적인 원인:**
1. StorageClass가 존재하지 않음 → 위의 "StorageClass 확인 및 수정" 참조
2. 스토리지 용량 부족 → 노드의 디스크 공간 확인
3. PV가 자동으로 프로비저닝되지 않음 → 클러스터에 Dynamic Provisioner 설정 필요

### Pod가 CrashLoopBackOff인 경우
```bash
# 로그 확인
kubectl logs redis-0 -n goormthon-6

# 이전 컨테이너 로그 확인
kubectl logs redis-0 -n goormthon-6 --previous
```

**일반적인 원인:**
1. 설정 파일 오류 → ConfigMap 확인
2. 권한 문제 → /data 디렉토리 권한 확인
3. 메모리 부족 → Resource limits 조정

## Redis 접속 테스트

### Pod 내부에서 접속
```bash
kubectl exec -it redis-0 -n goormthon-6 -- redis-cli ping
# 응답: PONG
```

### 다른 Pod에서 접속
```bash
kubectl run -it --rm redis-test --image=redis:7.2-alpine --restart=Never -n goormthon-6 -- redis-cli -h redis.goormthon-6.svc.cluster.local ping
# 응답: PONG
```

## 모니터링

### Redis 통계 확인
```bash
kubectl exec -it redis-0 -n goormthon-6 -- redis-cli INFO
```

### 리소스 사용량 확인
```bash
kubectl top pod redis-0 -n goormthon-6
```

## 백업 및 복구

### AOF 파일 위치
- 컨테이너 내부: `/data/appendonly.aof`
- PVC에 영구 저장

### 수동 백업
```bash
# AOF 파일 복사
kubectl exec redis-0 -n goormthon-6 -- cp /data/appendonly.aof /data/appendonly.aof.backup

# 로컬로 다운로드
kubectl cp goormthon-6/redis-0:/data/appendonly.aof ./appendonly.aof.backup
```
