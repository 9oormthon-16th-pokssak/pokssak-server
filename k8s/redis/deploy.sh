#!/bin/bash

# Redis 배포 스크립트
# Kustomize를 사용하여 Redis를 배포합니다.

set -e

NAMESPACE="${NAMESPACE:-goormthon-6}"
BASE_DIR="$(cd "$(dirname "$0")/base" && pwd)"

echo "==================================="
echo "Redis 배포 스크립트"
echo "Namespace: $NAMESPACE"
echo "Base Dir: $BASE_DIR"
echo "==================================="
echo ""

# 1. Dry-run으로 생성될 리소스 확인
echo "[1/3] Dry-run: 생성될 리소스 미리보기"
echo "-----------------------------------"
kubectl kustomize "$BASE_DIR"
echo ""

read -p "위의 리소스를 배포하시겠습니까? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "배포가 취소되었습니다."
    exit 0
fi
echo ""

# 2. 배포 실행
echo "[2/3] Redis 배포 중..."
kubectl apply -k "$BASE_DIR"
echo ""

# 3. 배포 상태 확인
echo "[3/3] 배포 상태 확인"
echo "-----------------------------------"
echo "대기 중... (최대 60초)"

# StatefulSet이 준비될 때까지 대기
kubectl wait --for=condition=ready pod/redis-0 \
    -n "$NAMESPACE" \
    --timeout=60s || true

echo ""
echo "현재 상태:"
kubectl get statefulset redis -n "$NAMESPACE"
kubectl get pod redis-0 -n "$NAMESPACE"
kubectl get pvc -l app=redis -n "$NAMESPACE"
echo ""

# Redis 연결 테스트
POD_STATUS=$(kubectl get pod redis-0 -n "$NAMESPACE" -o jsonpath='{.status.phase}' 2>/dev/null || echo "NotFound")

if [ "$POD_STATUS" = "Running" ]; then
    echo "✓ Pod가 Running 상태입니다"
    echo ""
    echo "Redis 연결 테스트 중..."
    if kubectl exec redis-0 -n "$NAMESPACE" -- redis-cli ping &>/dev/null; then
        echo "✓ Redis 연결 성공!"
    else
        echo "⚠ Redis 연결 실패"
        echo ""
        echo "로그 확인:"
        kubectl logs redis-0 -n "$NAMESPACE" --tail=20
    fi
else
    echo "⚠ Pod 상태: $POD_STATUS"
    echo ""
    echo "상세 정보는 다음 명령어로 확인하세요:"
    echo "  kubectl describe pod redis-0 -n $NAMESPACE"
    echo "  kubectl logs redis-0 -n $NAMESPACE"
fi

echo ""
echo "==================================="
echo "배포 완료"
echo "==================================="
echo ""
echo "유용한 명령어:"
echo "  # 상태 확인"
echo "  kubectl get all -l app=redis -n $NAMESPACE"
echo ""
echo "  # Redis 접속"
echo "  kubectl exec -it redis-0 -n $NAMESPACE -- redis-cli"
echo ""
echo "  # 로그 확인"
echo "  kubectl logs -f redis-0 -n $NAMESPACE"
echo ""
echo "  # 검증 스크립트 실행"
echo "  ./k8s/redis/verify-redis.sh"
