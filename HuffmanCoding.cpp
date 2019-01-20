//============================================================================
// Name        : HuffmanCoding.cpp
// Author      : lingo
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <stdio.h>
#include <malloc.h>
#include <string.h>
#define OK 1
#define Error 0
#define True 1
#define False 0
#define OverFlow -2

typedef int Status;
//顺序存储HuffmanTree，HT[0,...,n-1]
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
	HT = (HTNode *)malloc((m + 1)*sizeof(HTNode));//使用位置0，空出标识根结点 ，内存大小 +1 HTNode
	*HT = {0,0,0,0};
	int i = 1; HuffmanTree p = HT+1;
	for(; i < n+1; i++,p++,w++) *p = {*w, 0, 0, 0}; //从第二个元素开始存储初始叶子节点
	for(;i < m+1;i++,p++) *p = {0, 0, 0, 0};

	for(i = n + 1; i < m+1; i++){
		int s1,s2;
		// HT[0]未赋值，在HT[1,...,i-1]中选择parent不为0的两个weight最小的结点 s1,s2顺序存储索引
		Select(HT,i-1,&s1,&s2); //当i为n+1时 索引1到i-1中存储的为最开始的n个叶子结点
		//printf("s1=%d,s2=%d \n",s1,s2);
		HT[i].lchird = s1;HT[i].rchird = s2;
		HT[i].weight = HT[s1].weight + HT[s2].weight;
		HT[s1].parent = i;HT[s2].parent = i;
	}
	for(int i = 0; i < m+1; i++){
		printf("HT[%d] { weight = %d,parent = %d,lchird= %d,rchird= %d }\n",i,HT[i].weight,HT[i].parent,HT[i].lchird,HT[i].rchird);
	}
	return OK;
}
/**index   1   	   2      3      4      5      6       7      8        9         10        11
 *         2(p:7)  3(p:7) 4(p:8) 5(p:9) 6(p:9) 7(p:10) 5(p:8) 9(p:10)  11(p:11)  16(p:11)  27(p:0)
 *
 */
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

	return OK;
}

typedef char ** HuffmanCode; //动态分配字符串数组，存储Huffman编码表
/**
 *  HT,即已创建好的HuffmanTree,
 *  HC,赫夫曼编码表
 *  n,为叶子节点的个数，即编码的个数
 */
void HuffmanCoding(HuffmanTree HT,HuffmanCode &HC,int n){
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
	return;
}
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
