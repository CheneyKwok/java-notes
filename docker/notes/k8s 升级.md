# 升级流程

1、升级主master节点

2、升级其他master节点

3、升级node节点

## 升级 master

```java

// 查看可用版本
yum list --showduplicates kubeadm --disableexcludes=kubernetes

// 选择合适的升级版本（不能大于一个次要版本）
yum install -y kubeadm-1.20.15-0 --disableexcludes=kubernetes

// 验证升级计划
kubeadm upgrade plan

kubeadm upgrade apply v1.20.15

yum install -y kubelet-1.20.15-0 kubectl-1.20.15-0 --disableexcludes=kubernetes

systemctl daemon-reload

systemctl restart kubelet

```

## 升级 node

```java

kubeadm upgrade node

yum install -y kubelet-1.20.15-0 kubectl-1.20.15-0 --disableexcludes=kubernetes

systemctl daemon-reload

systemctl restart kubelet

```
