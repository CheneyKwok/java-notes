# K8s

## 部署

- 准备三个虚拟机（4 核 4G）

- 设置 Linux 环境

```java
// 关闭防火墙
systemctl stop firewalld
systemctl disable firewalld
// 关闭 selinux
sed -i 's/enforcing/disabled/' /etc/selinux/config
setenforce 0
// 关闭 swap
sed -ri 's/.*swap.*/#&/' /etc/fstab
// 添加主机名与 IP 对应关系 
vi /etc/hosts 
10.0.2.15 k8s-node1 
10.0.2.24 k8s-node2 
10.0.2.25 k8s-node3
// 指定新的 hostname
hostnamectl set-hostname <newhostname>
// 将桥接的 IPv4 流量传递到 iptables 的链： 
cat > /etc/sysctl.d/k8s.conf << EOF 
net.bridge.bridge-nf-call-ip6tables = 1 
net.bridge.bridge-nf-call-iptables = 1 
EOF 
sysctl --system
// 遇见提示是只读的文件系统，运行如下命令 
mount -o remount rw /
```

- 安装 Docker

- 添加阿里云 yum 源

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
yum install -y kubelet-1.17.3 kubeadm-1.17.3 kubectl-1.17.3
```

- 配置开机自启

```java
systemctl enable kubelet
```

- master 节点初始化

执行脚本安装 master 节点镜像

```java
./master_images.sh 
```

初始化 master 节点

```java
kubeadm init --apiserver-advertise-address=10.0.2.4 --image-repository registry.cn-hangzhou.aliyuncs.com/google_containers --kubernetes-version v1.17.3  --service-cidr=10.96.0.0/16 --pod-network-cidr=10.244.0.0/16
```

执行给出的提示命令

```java
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

保存其他节点加入 master 时需要用到的 token

```java
kubeadm join 10.0.2.4:6443 --token xrvmfd.oj5h7no23hsxoar4 \
    --discovery-token-ca-cert-hash sha256:235dde329ae0e15d32e35f99c95c74fe7d3b463f30bff38add0e25e1c801099f 
```

- 安装 Pod 网络插件

```java
kubectl apply -f kube-flannel.yml
```

```java
// 查看指定名称空间的 pods
kubectl get pods -n kube-system
// 查看所有名称空间
kubectl get ns
// 查看所有名称空间的 pods
kubectl get pods –all-namespaces
// 查看所有节点
kubectl get nodes
```

- 将其他 node 节点加入 master

执行之前的 token 即可

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
kubectl create deployment tomcat6 --image=tomcat:6.0.53-jre8 --drry-run -o yaml > tomcat6.yaml
```

生成 pod 的 yaml

```java
kubectl get pod <pod name> -o yaml > mypod.yaml
```
