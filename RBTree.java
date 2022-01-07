package com.jxust.lry2016;

import java.util.Objects;

/*
红黑树：一种平衡二叉树的实现（保持平衡的策略有：变色/旋转)
1)任何一个节点都有颜色，黑色或者红色
2)根节点是黑色的
3)父子节点之间不能出现两个连续的红节点
4)任何一个节点向下遍历到其子孙的叶子节点，所经过的黑节点个数必须相等
5)空节点被认为是黑色的
 */
public class RBTree<T extends Comparable<T>> {
    private Node<T> root;
    private int size;
    RBTree(){
        root = null;
        size = 0;
    }
    static final class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
        public T value;
        public Node<T> parent;
        public Node<T> left;
        public Node<T> right;
        public boolean isRed;
        Node(T value){
            this.value = value;
        }
        //为什么这样最终不用return
        Node<T> root(){
            for(Node<T> r = this,p;;){
                if((p = r.parent) == null)//只有一个出口
                    return r;
                r = p;
            }
            //为什么这样最终不用return，因为函数结束：只有一个出口
        }
        @Override
        public int compareTo(Node<T> o) {
            return this.value.compareTo(o.value);
        }
        @Override
        public int hashCode() {
            return Objects.hash(value, parent, left, right, isRed);
        }
    }
    private Node<T> nextNode(Node<T> p) {//BST后继节点
        Node<T> pr;
        if(p == null)
            return null;
        else if((pr = p.right) != null){//BST右子树最小节点，即右子树最左子为空节点
            Node<T> prl = pr;
            while(prl.left != null)
                prl = prl.left;
            return prl;
        }else{//p所在最早左支祖先
            Node<T> pp = p.parent;
            while(pp != null && p == pp.right){
                p = pp;
                pp = pp.parent;
            }
            return pp;
        }
    }
    boolean find(Node<T> p){
        for(Node<T> root = this.root;;){
            if(root == null)
                return false;
            int cmp = p.compareTo(root);
            if(cmp == 0)
                return p == root;//引用相同
            else if(cmp < 0)
                root = root.left;
            else
                root = root.right;
        }
//        Node<T> root = this.root;
//        while(root != null){
//            int cmp = p.compareTo(root);
//            if(cmp < 0)
//                root = root.left;
//            else if(cmp > 0)
//                root = root.right;
//            else return true;
//        }
//        Node<T> root = this.root;
//        return false;
    }
    boolean delete(Node<T> p){
        if(p == null || !find(p))
            return false;
        Node<T> pl,pr;
        //若左右非空，后继节点复制后删除除
        if((pl = p.left) != null && (pr = p.right) != null){//Case1
            Node<T> s = nextNode(p);
            p.value = s.value;
            p = s;//删除节点转移到后继节点
        }
        //删除节点并替换删除节点（重新调整二叉排序树过程）,其实经历过Case1后继p.left == null,若未满足case1,这句replace太秒了
        Node<T> replace = (p.left != null) ? p.left : p.right;//左非空选左，可能右空
        if(replace != null){//左右其中非空的
            replace.parent = p.parent;
            Node<T> pp;
            if((pp = p.parent) == null){//删除的为根
                root = replace;
            }else if(p == pp.left){
                pp.left = replace;
            }else
                pp.right = replace;
            p.left = p.right = p.parent = null;//GC
            if(p.isRed == false)//删除节点为黑色，替换后的调整过程, 已经少了一个
                root = balanceAfterDeletion(root,replace);
        }else{//左右都空
            if(p.parent == null){
                root = null;//根左右都空，引用置空后GC
            }else{//叶子节点直接删除
                if(p.isRed == false)//删除节点为黑色，删除前调整，多了一个，随后删除
                    root = balanceAfterDeletion(root,p);
                Node<T> pp;
                if((pp = p.parent) != null){
                    if(p == pp.left)
                        pp.left = null;
                    else
                        pp.right = null;
                    p.parent = null;//GC p
                }
            }
        }
        size--;
        return true;
    }
    boolean insert(Node<T> x){
        Node<T> parent = null,r = this.root;
        while(r != null){
            parent = r;
            int cmp = x.compareTo(r);
            if(cmp < 0){
                r = r.left;
            }else if(cmp > 0)
                r = r.right;
            else
                return false;
        }
        x.parent = parent;//找到叶子的父位置
        if(parent == null){
            x.isRed = false;
            this.root = x;
            return true;
        }else{
            //决定插入位置
            int cmp = x.compareTo(parent);
            if( cmp < 0)
                parent.left = x;
            else if(cmp > 0)
                parent.right = x;
            x.isRed = true;
        }
        size++;
        this.root = balanceAfterInsertion(this.root,x);
        return true;
    }
    Node<T> balanceAfterDeletion(Node<T> root,Node<T> x){
        //关注节点为：1）
        for(Node<T> xp,xpl,xpr;;){
            if(x == root || x == null){
                return root;
            }else if((xp = x.parent) == null){
                x.isRed = false;
                return x;
            }else if(x.isRed){
                x.isRed = false;
                return root;
            }
            //策略：1)减少兄弟路径黑色节点 2）增加当前路径黑色节点
            else if(x == (xpl = xp.left)){//关注节点为左子
                if((xpr = xp.right) != null && xpr.isRed){//Case 1:兄弟节点为红（旋转并调整颜色，黑色数量不变）
                    xpr.isRed = false; xp.isRed = true;//父兄节点换色
                    root = rotateLeft(root,xp);//左旋父节点
                    xpr = (xp = x.parent) == null ? null : xp.right;//关注节点不变，调整兄弟
                }
                if(xpr == null)//兄弟节点空，往上层跑，再借
                    x = xp;//转移关注节点
                else{
                    Node<T> bl = xpr.left,br = xpr.right;//兄弟的左子右子，侄子
                    if((bl == null || bl.isRed == false) &&
                       (br == null || br.isRed == false)){//Case2 : 两侄子都黑（
                        xpr.isRed = true;
                        x = xp;
                    }else{//其中之一不空（红）
                        if(br == null || br.isRed == false){//Case3 :左侄子红右侄子黑
                            //if(bl != null)
                                bl.isRed = false;//左侄子设黑
                            xpr.isRed = true;
                            root = rotateRight(root,xpr);
                            xpr = (xp = x.parent) == null ? null : xp.right;
                        }
                        //Case4 : 左侄子黑右侄子红
                        if(xpr != null){//Case4: 兄弟在
                            xpr.isRed = (xp == null) ? false : xp.isRed;//兄弟与父同色
                            //if((br = xpr.right) != null)
                                br.isRed = false;
                        }
                        if(xp != null){
                            xp.isRed = false;
                            root = rotateLeft(root,xp);
                        }
                        x = root;//结束
                    }
                }
            }else{
                if(xpl != null && xpl.isRed){
                    xpl.isRed = false;
                    xp.isRed = true;
                    root = rotateRight(root,xp);
                    xpl = (xp = x.parent) == null ? null : xp.left;
                }
                if(xpl == null)
                    x = xp;
                else{
                    Node<T> bl = xpl.left, br = xpl.right;
                    if((bl == null || bl.isRed == false) &&
                            (br == null || br.isRed == false)){//Case2 : 两侄子都黑（
                        xpl.isRed = true;
                        x = xp;
                    }else{//其中之一不空（红）
                        if(br == null || br.isRed == false){//Case3 :左侄子红右侄子黑
                            //if(bl != null)
                            bl.isRed = false;//左侄子设黑
                            xpl.isRed = true;
                            root = rotateRight(root,xpl);
                            xpl = (xp = x.parent) == null ? null : xp.left;
                        }
                        //Case4 : 左侄子黑右侄子红
                        if(xpl != null){//Case4: 兄弟在
                            xpl.isRed = (xp == null) ? false : xp.isRed;//兄弟与父同色
                            //if((br = xpl.right) != null)
                            br.isRed = false;
                        }
                        if(xp != null){
                            xp.isRed = false;
                            root = rotateLeft(root,xp);
                        }
                        x = root;//结束
                    }
                }
            }
        }
    }
    Node<T> balanceAfterInsertion(Node<T> root, Node<T> x) {
        //1)插入为根节点，xp == null
        //2)关注节点父节点为黑不用调整 xp.isBlack
        Node<T> xp,xpp;
        while((xp = x.parent) != null && xp.isRed && (xpp = xp.parent) != null){//关注节点父一定要是红才需要调整
            if(xp == xpp.left){//关注节点的父是祖父的左
                Node<T> xu;
                //情况1，关注节点的叔节点为红
                if((xu = xpp.right) != null && xu.isRed){
                    xp.isRed = false; xu.isRed = false;//父和叔都改黑
                    xpp.isRed = true;//祖父改红（一定是黑变红）
                    x = xpp;//关注节点转移到祖父节点（相当于当前插入位置，节点也为红，逐渐向上调整）
                }
                //情况2，关注节点的叔节点为黑(叔可以是NULL,也为黑，）
                else{
                    //情况2.1，关注节点是父右节点
                    if(x == xp.right){
                        root = rotateLeft(root,xp);
                        x = xp;
                    }
                    //情况2.2，关注节点是父左节点
                    xp.isRed = false;
                    xpp.isRed = true;
                    root = rotateRight(root,xpp);
                }
            }else{//关注节点的父是祖父的右
                Node<T> xu;
                //情况1，关注节点的叔节点为红
                if((xu = xpp.left) != null && xu.isRed){//(红，节点一定存在，不为NULL)
                    xp.isRed = false; xu.isRed = false;//父和叔都改黑
                    xpp.isRed = true;//祖父改红（一定是黑变红）
                    x = xpp;//关注节点转移到祖父节点（相当于当前插入位置，节点也为红，逐渐向上调整）
                }
                //情况2，关注节点的叔节点为黑(叔可以是NULL,也为黑，）
                else{
                    //情况2.1，关注节点是左节点
                    if(x == xp.left){
                        root = rotateRight(root,x = xp);
                    }
                    //情况2.2，关注节点是右节点
                    xp.isRed = false;
                    xpp.isRed = true;
                    root = rotateLeft(root,xpp);
                }
            }
        }
        return root;
    }
    static <T extends Comparable<T>> Node<T> rotateLeft(Node<T> root,Node<T> p){
        Node<T> r;
        if(p != null){
            r = p.right;
            if(r!= null && (p.right = r.left) != null)
                r.left.parent = p;
            r.parent = p.parent;
            if(p.parent == null){
                (root = r).isRed = false;
            }else if(p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
            /*
            Node<T> R,RL,PP;
            if(p != null && (R = p.right) != null){//这种写法可以避免空指针
                if((RL = p.right = R.left) != null)
                    R.left.parent = p;
                if((PP = R.parent = p.parent) == null)
                    (root = R).isRed = false;
                else if(PP.left == p)
                    PP.left = R;
                else PP.right = R;
                R.left = p;
                p.parent = R;
            }
             */
        return root;
    }
    static <T extends Comparable<T>> Node<T> rotateRight(Node<T> root,Node<T> p){
        Node<T> l,lr,pp;
        if(p != null && (l = p.left) != null){
            if((lr = p.left = l.right) != null)
                lr.parent = p;
            if((pp = l.parent = p.parent) == null)
                (root = l).isRed = false;
            else if(pp.left == p)
                pp.left = l;
            else pp.right = l;
            p.parent = l;
            l.right = p;
        }
        return root;
    }
    Node<T> preNodeFirst(){
        for(Node<T> p = root;;){
            if(p == null || p.left == null)
                return p;
            p = p.left;
        }
    }
    void print(){
        for(Node<T> p = preNodeFirst(); p != null; p = nextNode(p)){
            System.out.println(p.value + (p.isRed ? "red" : "black"));
        }
    }
    public static void main(String[] args) {
        RBTree<Integer> rbt = new RBTree<>();
        boolean flag;
        Node<Integer> tmp = new Node<>(3);
        flag = rbt.insert(tmp);
        flag = rbt.insert(new Node<>(1));
        flag = rbt.insert(new Node<>(2));
        flag = rbt.delete(tmp);//只能删除值相同，引用相同元素,可更改删除方式，自定义
        rbt.print();
    }
}
