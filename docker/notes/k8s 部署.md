# K8s

## 部署

- 准备三个虚拟机（必须 4 核 4G，否则 kubeadm 会 init 失败）

- 设置 Linux 环境

```java
// 关闭防火墙
systemctl stop firewalld && systemctl disable firewalld
// 关闭 selinux
sed -i 's/enforcing/disabled/' /etc/selinux/config && setenforce 0
// 关闭 swap
sed -ri 's/.*swap.*/#&/' /etc/fstab && swapoff -a
// 添加主机名与 IP 对应关系 
vi /etc/hosts 
192.168.3.101 k8s-node1
192.168.3.102 k8s-node2
192.168.3.103 k8s-node3
192.168.3.104 k8s-node4
192.168.3.105 k8s-node5
// 指定新的 hostname
hostnamectl set-hostname <newhostname>
// 将桥接的 IPv4 流量传递到 iptables 的链： 
cat > /etc/sysctl.d/k8s.conf << EOF 
net.bridge.bridge-nf-call-ip6tables = 1 
net.bridge.bridge-nf-call-iptables = 1 
net.ipv4.ip_forward = 1
EOF 
sysctl --system
// 遇见提示是只读的文件系统，运行如下命令 
mount -o remount rw /
```

- 安装 Docker

- 添加 k8s yum 源

```java
cat > /etc/yum.repos.d/kubernetes.repo << EOF 
[kubernetes] 
name=Kubernetes 
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0 
repo_gpgcheck=0 
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg 
https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg 
EOF
```

- 检查 yum 源中是否有与 kube 相关的安装源

```java
yum list | grep kube
```

- 安装 kubelet、kubeadm、kubectl

```java
yum install -y kubelet-1.17.9 kubeadm-1.17.9 kubectl-1.17.9 
```

- 配置开机自启

```java
systemctl enable kubelet
systemctl start kubelet
```

- master 节点初始化

执行脚本安装 master 节点镜像

kubeadm --kubernetes-version 1.17.3 config images list

```java
./master_images.sh 
// 删除 \r 换行符
sed -i 's/\r//' master_images.sh
```

初始化 master 节点

```java
kubeadm init \
--apiserver-advertise-address=10.0.2.6 \
--image-repository registry.cn-hangzhou.aliyuncs.com/google_containers \
--kubernetes-version v1.17.9  \
--service-cidr=10.96.0.0/16 -\
-pod-network-cidr=192.168.0.0/16 

10.244.0.0/16
```

执行给出的提示命令

```java
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

保存其他节点加入 master 时需要用到的 token

```java
kubeadm join 10.0.2.6:6443 --token 588feh.ds0684qnd32pim7p \
    --discovery-token-ca-cert-hash sha256:541a706ac564cfe63fb61539445a8ed711478b45000d022e11eb8fd6f669d112 

// 生成 token 
kubeadm token create --ttl 0 --print-join-command
```

- 安装 Pod 网络插件

```java

curl https://docs.projectcalico.org/v3.8/manifests/calico.yaml -O

kubectl apply -f calico.yml

// kubectl apply -f kube-flannel.yml
```

```java
// 查看指定名称空间的 pods
kubectl get pods -n kube-system
// 查看所有名称空间
kubectl get ns
// 查看所有名称空间的 pods
kubectl get pods -–all-namespaces
// 查看所有节点
kubectl get nodes

```

- 监控所有节点的 pod 进度

```java
watch kubectl get pod -n kube-system -o wide
```

## 入门操作

- 部署一个 tomcat

```java
kubectl create deployment tomcat6 --image=tomcat:6.0.53-jre8
```

监控部署详情

```java
kubectl get pods -o wide

kubectl get all -o wide
```

- 暴露访问

```java
kubectl expose deployment tomcat6 --port=80 --target-port=8080 --type=NodePort
```

查看服务暴露的端口

```java
kubectl get svc
```

- 动态扩容

```java
kubectl scale --replicas=3 deployment tomcat6
```

缩容

```java
kubectl scale --replicas=1 deployment tomcat6
```

- 删除资源

```java
kubectl get all
kubectl delete deployment.apps/tomcat6
kubectl delete service/tomcat6
```

## k8s yaml 基本使用

以测试部署 (--dry-run)的方式生产 yaml (-o yaml)

```java
kubectl create deployment tomcat6 --image=tomcat:6.0.53-jre8 --dry-run -o yaml > tomcat6-deployment.yaml

kubectl expose deployment tomcat6 --port=80 --target-port=8080 --type=NodePort --dry-run -o yaml
```

生成 pod 的 yaml

```java
kubectl get pod <pod name> -o yaml > mypod.yaml
```

将部署和暴露合并为一个 yaml

```java
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: tomcat6
  name: tomcat6
spec:
  replicas: 1
  selector:
    matchLabels:
      app: tomcat6
  template:
    metadata:
      labels:
        app: tomcat6
    spec:
      containers:
      - image: tomcat:6.0.53-jre8
        name: tomcat
---
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: tomcat6
  name: tomcat6
spec:
  ports:
  - port: 80
    protocol: TCP
    targetPort: 8080
  selector:
    app: tomcat6
  type: NodePort
```

## Ingress

Ingress 公开从集群外部到集群内服务的 HTTP 和 HTTPS 路由。 流量路由由 Ingress 资源上定义的规则控制。

Ingress 可为 Service 提供外部可访问的 URL、负载均衡流量、终止 SSL/TLS，以及基于名称的虚拟托管，基于 nginx 实现。

- 部署 Ingress Controller

```java
kubectl apply -f ingress-controller.yaml
```

- 创建 一个用于访问 tomcat 的 ingress 规则

```java
vi ingress-tomcat6.yaml
```

```java
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: web
spec:
  rules: 
  - host: tomcat6.atguigu.com
    http:
      paths: 
        - backend:
           serviceName: tomcat6
           servicePort: 80
```

应用规则

```java
vi ingress-tomcat6.yaml
```

## kubershpere

### 安装helm（根据官网指示）

### 安装 Tiller

Tiller是Helm的服务端组件。Tiller将被安装在kubernetes集群中，Helm客户端会与其交互，从而使用Helm charts部署应用程序

为 Tiller 创建具有集群管理员权限的Service Account

执行 kubectl apply -f helm-rbac.yaml

```java
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: kube-system

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
   name: tiller
roleRef:
   apiGroup: rbac.authorization.k8s.io
   kind: ClusterRole
   name: cluster-admin
subjects: 
  - kind: ServiceAccount
    name: tiller
    namespace: kube-system
```

安装 tiller

```java

helm init --upgrade -i registry.cn-hangzhou.aliyuncs.com/google_containers/tiller:v2.16.2 --stable-repo-url https://kubernetes.oss-cn-hangzhou.aliyuncs.com/charts --service-account tiller

```

安装 StorageClass (openebs）

kubeshpere 的redis 一直 pending 时记得去除 master 的污点

```java
kubectl describe node master | grep Taint

kubectl taint nodes master node-role.kubernetes.io/master:NoSchedule-

// 添加污点
kubectl taint nodes master node-role.kubernetes.io/master=:PreferNoSchedule

```

查看 kube-state-metrics ServiceAccount

```java
kubectl get serviceaccounts/kube-state-metrics -n kubesphere-monitoring-system -o yaml
```

修改 kube-state-metrics 的 rbac

```java
kubectl edit clusterroles/kubesphere-kube-state-metrics -o yaml
```

kube-state-metrics 的 rbac

```java
rules:
- apiGroups:
  - ""
  resources:
  - configmaps
  - secrets
  - nodes
  - pods
  - services
  - resourcequotas
  - replicationcontrollers
  - limitranges
  - persistentvolumeclaims
  - persistentvolumes
  - namespaces
  - endpoints
  verbs:
  - list
  - watch
- apiGroups:
  - extensions
  resources:
  - daemonsets
  - deployments
  - replicasets
  - ingresses
  verbs:
  - list
  - watch
- apiGroups:
  - apps
  resources:
  - statefulsets
  - daemonsets
  - deployments
  - replicasets
  verbs:
  - list
  - watch
- apiGroups:
  - batch
  resources:
  - cronjobs
  - jobs
  verbs:
  - list
  - watch
- apiGroups:
  - autoscaling
  resources:
  - horizontalpodautoscalers
  verbs:
  - list
  - watch
- apiGroups:
  - authentication.k8s.io
  resources:
  - tokenreviews
  verbs:
  - create
- apiGroups:
  - authorization.k8s.io
  resources:
  - subjectaccessreviews
  verbs:
  - create
- apiGroups:
  - policy
  resources:
  - poddisruptionbudgets
  verbs:
  - list
  - watch
  
- apiGroups:
  - certificates.k8s.io
  resources:
  - certificatesigningrequests
  verbs:
  - list
  - watch
- apiGroups:
  - storage.k8s.io
  resources:
  - storageclasses
  - volumeattachments
  verbs:
  - list
  - watch
- apiGroups:
  - admissionregistration.k8s.io
  resources:
  - mutatingwebhookconfigurations
  - validatingwebhookconfigurations
  verbs:
  - list
  - watch
- apiGroups:
  - networking.k8s.io
  resources:
  - networkpolicies
  verbs:
  - list
  - watch
- apiGroups:
  - coordination.k8s.io
  resources:
  - leases
  verbs:
  - list
  - watch
```

查看 ks-install 日志

```java
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f

```

### 究极大坑

1. 当 pod 内网络 无法 ping 外网时，是由于 coredns pod 解析 dns 失败

```java
对部署了 coredns pod 的 node 节点修改 resolv.conf
// 添加 nameserver 8.8.8.8
vi /etc/resolv.conf 

// 删除 coredns pod

kubectl delete coredns-7c56fbdb87-vxvlg  -n kube-system
```

1. 当 coreDNS pod 一直 p处于 pending 状态

查看 kubelet 日志

```java
journalctl -f -u kubelet.service

failed to find plugin “flannel” in path [/opt/cni/bin]
```

查看 /opt/cni/bin 缺少 flannel

则需要下载 CNI 插件：CNI plugins v0.8.6

github下载地址：https://github.com/containernetworking/plugins/releases/tag/v0.8.6

(在1.0.0版本后CNI Plugins中没有 flannel)

将 flannel mv 至 /opt/cni/bin （所有 k8s node 都需要）
