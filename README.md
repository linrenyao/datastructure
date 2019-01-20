### HuffmanTree和HuffmanCoding

##### 1.HuffmanTree

> HuffmanTree又称为最优二叉树，是一类带权路径长度最优的树。
>
> 假设有n结点，权值为{w1,w2,...,wn},构造一颗有n个叶子结点的二叉树，每个叶子结点带权为wi,则其中带权路径长度WPL最小的二叉树称做最优二叉树(HuffmanTree).

1.1 如何构造HuffmanTree

赫夫曼算法：

> 1. 根据给定的n个权值{w1,w2,...,wn}构造成n颗二叉树的集合F={T1,T2,...,Tn},其中每颗二叉树Ti中只有一个权值wi的根结点，其左右结点均空。
> 2. 在F中选取两颗根结点的权值最小的树作为左右子树构造一颗新的二叉树，且置新的二叉树根结点的权值为左右子树根节点的权值之和。
> 3. 在F中删除这两颗树，同时将新得到的二叉树加F集合中。
> 4. 循环执行2，3直到集合F中只含一颗树为止。这颗树便是赫夫曼树

1.2 HuffmanTree的实现

n个叶子结点，一颗HuffmanTree结点个数为2n -1 个，顺序存储所有结点所需的存储空间为(2n-1) * sizeof(HTNode),前n个存储空间存储n个叶子结点，后n-1个存储空间存储非叶子结点。

```c
//顺序存储HuffmanTree
typedef struct{
	int weight; //结点权值
	int parent,lchird,rchird; //指定结点的索引值
}HTNode,* HuffmanTree;

Status Select(HuffmanTree HT,int endindex,int *s1,int *s2);
/**
 *
 *  *w = weight[] ,各字符的权值
 *   n ，字符的个数 ,即n个叶子结点
 */
Status createHuffmanTree(HuffmanTree &HT,int *w,int n){
	if(n < 1) return False;

	int m = 2*n - 1;  // n个结点 生成一颗最优二叉树所以结点个数
	HT = (HTNode *)malloc((m + 1)*sizeof(HTNode));//使用位置0空出标识根结点
	*HT = {0,0,0,0};
	int i = 1; HuffmanTree p = HT+1;
	for(; i < n+1; i++,p++,w++) *p = {*w, 0, 0, 0}; //从第二个元素开始存储初始叶子节点
	for(;i < m+1;i++,p++) *p = {0, 0, 0, 0};

	for(i = n + 1; i < m+1; i++){
		int s1,s2;
		// HT[0]未赋值，在HT[1,...,i-1]中选择parent不为0的两个weight最小的结点 s1,s2顺序存储索引
		Select(HT,i-1,&s1,&s2); //当i为n+1时 索引1到i-1中存储的为最开始的n个叶子结点
		HT[i].lchird = s1;HT[i].rchird = s2;
		HT[i].weight = HT[s1].weight + HT[s2].weight;
		HT[s1].parent = i;HT[s2].parent = i;
	}
	for(int i = 1; i < m+1; i++){
		printf("HT[%d] { weight = %d,parent = %d,lchird= %d,rchird= %d }\n",i,HT[i].weight,HT[i].parent,HT[i].lchird,HT[i].rchird);
	}
	return OK;
}
Status Select(HuffmanTree HT,int endindex,int *s1,int *s2){
	int indexnum = 0;
	for(int i = 1; i <=endindex;i++){
		if(HT[i].parent == 0) indexnum++;
	}
	int index[indexnum];
	for(int i = 1,j=0; i <=endindex;i++){
		if(HT[i].parent == 0) index[j++] = i;
	}
	int minindex = index[0];
	for(int i = 1; i < indexnum;i++){
		if(HT[index[i]].weight < HT[minindex].weight) minindex = index[i];
	}
	*s1 = minindex;

	minindex = index[0];
	if(minindex == *s1) minindex=index[1];
	for(int i = minindex; i < indexnum;i++){
		if(i != *s1){
			if(HT[index[i]].weight <= HT[minindex].weight) minindex = index[i];
		}
	}
	*s2 = minindex;

```

2.HuffmanCoding

2.1HuffmanCoding

> 赫夫曼编码，使用一张编码表将源字符编码，编码表中，出现概率高的使用短编码，概率低使用长编码
>
> HuffmanCoding用于数据无损压缩 
>
> 例：压缩字符串 'ABACCDA' 总共四种字符
>
> 若以二进制编码，两位二进制即刻编码，A:00 B:01 C:10 D:11 ，编码结果为 ‘000110101101’ ，解码按两位
>
> 若想用更短的编码表示压缩的字符串，将重复多的使用短编码，重复少的使用长编码。
>
> 则有编码 A:0 B:00 C:1 D:01 ,编码结果 ‘000011010’  ，但会有多种情况译码
>
> 这时我们便要设计长短不等，任何一个字符编码都不是另一个字符编码的中前缀，这种编码称**前缀编码**
>
> 这时我们用构造HuffmanTree，约定左分支为0，右分支为1
>
> 得到前缀编码，A:0 B:101 C:10 D:111

2.2 HuffmanCoding实现

```c
typedef char ** HuffmanCode; //动态分配字符串数组，存储Huffman编码表
/**
 *  HT,即已创建好的HuffmanTree,
 *  HC,赫夫曼编码表
 *  n,为叶子节点的个数，即编码的个数
 */
Status HuffmanCoding(HuffmanTree HT,HuffmanCode &HC,int n){
	//HT叶子节点从索引1开始
	//重叶子到根逆向求每个字符的赫夫曼编码
	HC = (HuffmanCode)malloc((n+1)*sizeof(char *)); //分配n+1,从1开始存储字符编码的指针向量
	char *cd = (char *)malloc(n*sizeof(char)); //一个字符编码的最大编码空间,n-1个空间存储最多二进制，最后一个存储结束字符
	cd[n-1] = '\0';
	for(int i = 1; i < n+1; i++){
		int start = n-1;
		for(int c = i,f = HT[i].parent; f != 0; f = HT[f].parent ){
			if(HT[f].lchird == c) cd[--start] = '0';
			else cd[--start] = '1';
		}
		HC[i] = (char*)malloc((n-start)*sizeof(char));
		strcpy(HC[i],&cd[start]);
	}
	free(cd);
	return OK;
}
```



3.Test

```c
int main() {
	HuffmanTree ht;
	int weight[6] = {2,3,4,5,6,7};
	createHuffmanTree(ht,weight,6);
	HuffmanCode hc;
	HuffmanCoding(ht,hc,6);
	for(int i = 1; i < 7; i++){
		printf("hc[%d]:%s \n",i,hc[i]);
	}
	return 0;
}
```

result:

```
HT[0] { weight = 0,parent = 0,lchird= 0,rchird= 0 }
HT[1] { weight = 2,parent = 7,lchird= 0,rchird= 0 }
HT[2] { weight = 3,parent = 7,lchird= 0,rchird= 0 }
HT[3] { weight = 4,parent = 8,lchird= 0,rchird= 0 }
HT[4] { weight = 5,parent = 9,lchird= 0,rchird= 0 }
HT[5] { weight = 6,parent = 9,lchird= 0,rchird= 0 }
HT[6] { weight = 7,parent = 10,lchird= 0,rchird= 0 }
HT[7] { weight = 5,parent = 8,lchird= 1,rchird= 2 }
HT[8] { weight = 9,parent = 10,lchird= 3,rchird= 7 }
HT[9] { weight = 11,parent = 11,lchird= 4,rchird= 5 }
HT[10] { weight = 16,parent = 11,lchird= 6,rchird= 8 }
HT[11] { weight = 27,parent = 0,lchird= 9,rchird= 10 }
hc[1]:1110 
hc[2]:1111 
hc[3]:110 
hc[4]:10 
hc[5]:11 
hc[6]:10 
```

