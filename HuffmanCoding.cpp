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
//˳��洢HuffmanTree��HT[0,...,n-1]
typedef struct{
	int weight; //���Ȩֵ
	int parent,lchird,rchird; //ָ����������ֵ
}HTNode,* HuffmanTree;

Status Select(HuffmanTree HT,int endindex,int *s1,int *s2);

/**
 *
 *  *w = weight[] ,���ַ���Ȩֵ
 *   n ���ַ��ĸ��� ,��n��Ҷ�ӽ��
 */
Status createHuffmanTree(HuffmanTree &HT,int *w,int n){
	if(n < 1) return False;

	int m = 2*n - 1;  // n����� ����һ�����Ŷ��������Խ�����
	HT = (HTNode *)malloc((m + 1)*sizeof(HTNode));//ʹ��λ��0���ճ���ʶ����� ���ڴ��С +1 HTNode
	*HT = {0,0,0,0};
	int i = 1; HuffmanTree p = HT+1;
	for(; i < n+1; i++,p++,w++) *p = {*w, 0, 0, 0}; //�ӵڶ���Ԫ�ؿ�ʼ�洢��ʼҶ�ӽڵ�
	for(;i < m+1;i++,p++) *p = {0, 0, 0, 0};

	for(i = n + 1; i < m+1; i++){
		int s1,s2;
		// HT[0]δ��ֵ����HT[1,...,i-1]��ѡ��parent��Ϊ0������weight��С�Ľ�� s1,s2˳��洢����
		Select(HT,i-1,&s1,&s2); //��iΪn+1ʱ ����1��i-1�д洢��Ϊ�ʼ��n��Ҷ�ӽ��
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

typedef char ** HuffmanCode; //��̬�����ַ������飬�洢Huffman�����
/**
 *  HT,���Ѵ����õ�HuffmanTree,
 *  HC,�շ��������
 *  n,ΪҶ�ӽڵ�ĸ�����������ĸ���
 */
void HuffmanCoding(HuffmanTree HT,HuffmanCode &HC,int n){
	//HTҶ�ӽڵ������1��ʼ
	//��Ҷ�ӵ���������ÿ���ַ��ĺշ�������
	HC = (HuffmanCode)malloc((n+1)*sizeof(char *)); //����n+1,��1��ʼ�洢�ַ������ָ������
	char *cd = (char *)malloc(n*sizeof(char)); //һ���ַ������������ռ�,n-1���ռ�洢�������ƣ����һ���洢�����ַ�
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
