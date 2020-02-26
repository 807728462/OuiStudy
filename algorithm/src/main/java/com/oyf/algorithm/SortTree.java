package com.oyf.algorithm;

/**
 * @创建者 oyf
 * @创建时间 2020/1/6 11:49
 * @描述
 **/
public class SortTree {
    public static void main(String[] args) {
        int[] data = new int[]{10,
                5, 15,
                1, 6, 12, 32};
        BalanceSortTree balanceSortTree = new BalanceSortTree();
        for (int i = 0; i < data.length; i++) {
            balanceSortTree.add(new Node(data[i]));
        }
        balanceSortTree.middle();
        log("----删除----");
        balanceSortTree.delNode(1);
        balanceSortTree.delNode(5);
        balanceSortTree.delNode(12);
        balanceSortTree.middle();
    }

    static class BalanceSortTree {
        Node root;

        public void add(Node node) {
            if (root == null) {
                root = node;
                return;
            }
            root.add(node);
        }

        /**
         * 删除节点
         *
         * @param value
         */
        public void delNode(int value) {
            if (root == null) {
                return;
            }
            //先查找出当前节点
            Node targetNode = searchNode(value);
            if (targetNode == null) {
                log("没有找到当前节点");
                return;
            }
            //查找出当前的节点的父节点
            Node parentNode = searchParentNode(value);
            if (parentNode == null) {
                log("父节点为空");
            }

            if (targetNode.left == null && targetNode.right == null) {//删除叶子节点
                //确定targetNode是父节点得左节点还是右节点
                if (parentNode != null && parentNode.left != null && parentNode.left.value == value) {
                    parentNode.left = null;
                } else if (parentNode != null && parentNode.right != null && parentNode.right.value == value) {
                    parentNode.right = null;
                } else {
                    root = null;
                }
            } else if (targetNode.left != null && targetNode.right != null) {
               /* 3，从targetNode的右子树找到最小的节点 temp（左子树的就找最大的）
                4，删除最小节点
                5，targetNode.value=temp*/
                Node rightMin = searchRightMin(targetNode);
                delNode(rightMin.value);
                targetNode.value = rightMin.value;
            } else {
                if (parentNode != null && parentNode.left != null && parentNode.left.value == value) {
                    if (targetNode.left != null) {
                        parentNode.left = targetNode.left;
                    } else if (targetNode.right != null) {
                        parentNode.left = targetNode.right;
                    }
                } else if (parentNode != null && parentNode.right != null && parentNode.right.value == value) {
                    if (targetNode.right != null) {
                        parentNode.right = targetNode.right;
                    } else if (targetNode.left != null) {
                        parentNode.right = targetNode.left;
                    }
                } else {
                    if (targetNode.left != null) {
                        root = targetNode.left;
                    } else if (targetNode.right != null) {
                        root = targetNode.right;
                    }
                }
            }

        }

        public Node searchNode(int value) {
            return root.searchNode(value);
        }

        public Node searchParentNode(int value) {
            return root.searchParentNode(value);
        }

        public Node searchRightMin(Node node) {
            if (node == null) {
                return null;
            }
            Node temp = node;
            if (temp.right == null) {
                return null;
            }
            temp = temp.right;

            while (temp.left != null) {
                temp = temp.left;
            }
            return temp;
        }

        public void middle() {
            if (root == null) {

                return;
            }
            root.middle();
        }
    }


    static class Node {
        int value;
        Node left;
        Node right;

        public Node(int data) {
            this.value = data;
        }

        /**
         * 添加一个节点，并且是有序的   小于当前的加入左节点   大于等于加入右节点
         *
         * @param node
         */
        public void add(Node node) {
            if (node == null) return;
            if (node.value < this.value) {
                if (this.left == null) {
                    this.left = node;
                } else {
                    this.left.add(node);
                }
            } else {
                if (this.right == null) {
                    this.right = node;
                } else {
                    this.right.add(node);
                }
            }
        }

        /**
         * 查询当前的node
         *
         * @param value 需要搜索的值
         * @return
         */
        public Node searchNode(int value) {
            if (this.value == value) {
                return this;
            } else if (this.value < value) {
                return this.right.searchNode(value);
            } else if (this.value > value) {
                return this.left.searchNode(value);
            }
            return null;
        }

        /**
         * 查找节点的父节点
         *
         * @param value 需要查找的值
         * @return
         */
        public Node searchParentNode(int value) {
            if (((this.left != null) && (this.left.value == value)) || ((this.right != null) && (this.right.value == value))) {
                return this;
            }
            Node node = null;
            if (this.left != null) {
                node = this.left.searchParentNode(value);
                if (node != null) return node;
            }
            if (this.right != null) {
                node = this.right.searchParentNode(value);
            }
            return node;
        }

        /**
         * 中序遍历
         */
        public void middle() {
            if (this.left != null) {
                this.left.middle();
            }
            System.out.println("节点=" + value);
            if (this.right != null) {
                this.right.middle();
            }
        }
    }

    public static void log(String text) {
        System.out.println(text);
    }
}
