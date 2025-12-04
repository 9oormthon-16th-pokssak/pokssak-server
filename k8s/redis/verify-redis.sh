#!/bin/bash

# Redis StatefulSet 검증 스크립트
# 이 스크립트는 Redis 배포 상태를 확인하고 문제를 진단합니다.

set -e

NAMESPACE="${NAMESPACE:-goormthon-6}"
REDIS_NAME="${REDIS_NAME:-redis}"

echo "==================================="
echo "Redis StatefulSet 검증 시작"
echo "Namespace: $NAMESPACE"
echo "==================================="
echo ""

# 1. Namespace 확인
echo "[1/8] Namespace 확인..."
if kubectl get namespace "$NAMESPACE" &>/dev/null; then
    echo "✓ Namespace '$NAMESPACE' 존재"
else
    echo "✗ Namespace '$NAMESPACE'가 존재하지 않습니다"
    echo "  생성 방법: kubectl create namespace $NAMESPACE"
    exit 1
fi
echo ""

# 2. StorageClass 확인
echo "[2/8] StorageClass 확인..."
if kubectl get storageclass gp2 &>/dev/null; then
    echo "✓ StorageClass 'gp2' 존재"
else
    echo "⚠ StorageClass 'gp2'가 존재하지 않습니다"
    echo ""
    echo "사용 가능한 StorageClass 목록:"
    kubectl get storageclass
    echo ""
    echo "대체 방법:"
    echo "  1. statefulset.yaml에서 storageClassName을 위의 목록 중 하나로 변경"
    echo "  2. 또는 storageClassName을 제거하여 기본 StorageClass 사용"
    echo ""
    read -p "계속하시겠습니까? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi
echo ""

# 3. ConfigMap 확인
echo "[3/8] ConfigMap 확인..."
if kubectl get configmap redis-config -n "$NAMESPACE" &>/dev/null; then
    echo "✓ ConfigMap 'redis-config' 존재"
else
    echo "✗ ConfigMap 'redis-config'가 존재하지 않습니다"
    echo "  생성 필요: kubectl apply -f k8s/redis/base/configmap.yaml -n $NAMESPACE"
fi
echo ""

# 4. Service 확인
echo "[4/8] Service 확인..."
if kubectl get service "$REDIS_NAME" -n "$NAMESPACE" &>/dev/null; then
    echo "✓ Service '$REDIS_NAME' 존재"
    kubectl get service "$REDIS_NAME" -n "$NAMESPACE"
else
    echo "✗ Service '$REDIS_NAME'가 존재하지 않습니다"
fi
echo ""

# 5. StatefulSet 확인
echo "[5/8] StatefulSet 확인..."
if kubectl get statefulset "$REDIS_NAME" -n "$NAMESPACE" &>/dev/null; then
    echo "✓ StatefulSet '$REDIS_NAME' 존재"
    kubectl get statefulset "$REDIS_NAME" -n "$NAMESPACE"
    echo ""

    # Replicas 상태 확인
    DESIRED=$(kubectl get statefulset "$REDIS_NAME" -n "$NAMESPACE" -o jsonpath='{.spec.replicas}')
    READY=$(kubectl get statefulset "$REDIS_NAME" -n "$NAMESPACE" -o jsonpath='{.status.readyReplicas}')

    if [ "$READY" = "$DESIRED" ]; then
        echo "✓ 모든 Replicas가 준비됨 ($READY/$DESIRED)"
    else
        echo "⚠ Replicas가 준비되지 않음 (${READY:-0}/$DESIRED)"
    fi
else
    echo "✗ StatefulSet '$REDIS_NAME'가 존재하지 않습니다"
fi
echo ""

# 6. Pod 확인
echo "[6/8] Pod 확인..."
POD_NAME="${REDIS_NAME}-0"
if kubectl get pod "$POD_NAME" -n "$NAMESPACE" &>/dev/null; then
    echo "✓ Pod '$POD_NAME' 존재"
    kubectl get pod "$POD_NAME" -n "$NAMESPACE"
    echo ""

    # Pod 상태 확인
    POD_STATUS=$(kubectl get pod "$POD_NAME" -n "$NAMESPACE" -o jsonpath='{.status.phase}')

    if [ "$POD_STATUS" = "Running" ]; then
        echo "✓ Pod가 Running 상태"
    else
        echo "⚠ Pod 상태: $POD_STATUS"
        echo ""
        echo "Pod 상세 정보:"
        kubectl describe pod "$POD_NAME" -n "$NAMESPACE" | tail -20
    fi
else
    echo "✗ Pod '$POD_NAME'가 존재하지 않습니다"
fi
echo ""

# 7. PVC 확인
echo "[7/8] PersistentVolumeClaim 확인..."
PVC_NAME="data-${REDIS_NAME}-0"
if kubectl get pvc "$PVC_NAME" -n "$NAMESPACE" &>/dev/null; then
    echo "✓ PVC '$PVC_NAME' 존재"
    kubectl get pvc "$PVC_NAME" -n "$NAMESPACE"
    echo ""

    # PVC 상태 확인
    PVC_STATUS=$(kubectl get pvc "$PVC_NAME" -n "$NAMESPACE" -o jsonpath='{.status.phase}')

    if [ "$PVC_STATUS" = "Bound" ]; then
        echo "✓ PVC가 Bound 상태"
    else
        echo "⚠ PVC 상태: $PVC_STATUS"
        echo ""
        echo "PVC 상세 정보:"
        kubectl describe pvc "$PVC_NAME" -n "$NAMESPACE" | tail -20
    fi
else
    echo "✗ PVC '$PVC_NAME'가 존재하지 않습니다"
fi
echo ""

# 8. Redis 연결 테스트
echo "[8/8] Redis 연결 테스트..."
if kubectl get pod "$POD_NAME" -n "$NAMESPACE" &>/dev/null; then
    POD_STATUS=$(kubectl get pod "$POD_NAME" -n "$NAMESPACE" -o jsonpath='{.status.phase}')

    if [ "$POD_STATUS" = "Running" ]; then
        echo "Redis PING 테스트 중..."
        if kubectl exec "$POD_NAME" -n "$NAMESPACE" -- redis-cli ping &>/dev/null; then
            echo "✓ Redis 연결 성공 (PING -> PONG)"

            # Redis INFO
            echo ""
            echo "Redis 정보:"
            kubectl exec "$POD_NAME" -n "$NAMESPACE" -- redis-cli INFO server | grep -E "redis_version|uptime_in_seconds|process_id"
            echo ""
            kubectl exec "$POD_NAME" -n "$NAMESPACE" -- redis-cli INFO persistence | grep -E "aof_enabled|aof_current_size"
        else
            echo "✗ Redis 연결 실패"
            echo ""
            echo "Pod 로그:"
            kubectl logs "$POD_NAME" -n "$NAMESPACE" --tail=30
        fi
    else
        echo "⚠ Pod가 Running 상태가 아니어서 연결 테스트를 건너뜁니다"
    fi
else
    echo "⚠ Pod가 존재하지 않아 연결 테스트를 건너뜁니다"
fi
echo ""

echo "==================================="
echo "검증 완료"
echo "==================================="
