//  Copyright 2013 Google Inc. All Rights Reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#include <stdio.h>
#include <string.h>
#include <math.h>
#include <stdlib.h>

const long long max_size = 2000;         // max length of strings
const long long N = 40;                  // number of closest words that will be shown
const long long max_w = 50;              // max length of vocabulary entries

int main(int argc, char **argv) {
  FILE *f, *fin, *fout;
  //char st1[max_size], st2[max_size];
  char vector_file_name[max_size];
  char pairs_file_name[max_size], output_file_name[max_size];
  float dist, len, vec1[max_size], vec2[max_size];
  long long words, size, a, b, c, d;
  char ch;
  float *M;
  char *vocab;
  char *st1,*st2,*st3,*st4,*st5;
  if (argc != 4) {
    printf("Usage: ./distance-pairs <VECTOR_FILE> string1 string2\nwhere VECTOR_FILE contains word projections in the BINARY FORMAT\n");
    return 0;
  }
  strcpy(vector_file_name, argv[1]);
  f = fopen(vector_file_name, "rb");
  if (f == NULL) {
    printf("Vector file not found\n");
    return -1;
  }
  fscanf(f, "%lld", &words);
  fscanf(f, "%lld", &size);
  vocab = (char *)malloc((long long)words * max_w * sizeof(char));
  M = (float *)malloc((long long)words * (long long)size * sizeof(float));
  if (M == NULL) {
    printf("Cannot allocate memory: %lld MB    %lld  %lld\n", (long long)words * size * sizeof(float) / 1048576, words, size);
    return -1;
  }
  for (b = 0; b < words; b++) {
    fscanf(f, "%s%c", &vocab[b * max_w], &ch);
    for (a = 0; a < size; a++) fread(&M[a + b * size], sizeof(float), 1, f);
    len = 0;
    for (a = 0; a < size; a++) len += M[a + b * size] * M[a + b * size];
    len = sqrt(len);
    for (a = 0; a < size; a++) M[a + b * size] /= len;
  }
  fclose(f);

  //strcpy(pairs_file_name, argv[2]);
  st1=argv[2];
  st2=argv[3];
  st3 = malloc(strlen(st1)+strlen(st2)+2);
  st4 = malloc(strlen(st1)+strlen(st2)+2);
  st5="_";
  strcpy(st3, st1);
  strcat(st3, st5);
  strcat(st3, st2);
  strcpy(st4, st2);
  strcat(st4, st5);
  strcat(st4, st1);

  for (b = 0; b < words; b++) if (!strcmp(&vocab[b * max_w], st3)) break;
  if (b == words) b = -1;
  for (c = 0; c < words; c++) if (!strcmp(&vocab[c * max_w], st4)) break;
  if (c == words) c = -1;

  if (b == -1 && c == -1) printf("%lld\n",b);
  else if (b != -1 && c == -1) printf("%lld\n",b);
  else if (b == -1 && c != -1) printf("%lld\n",c);
  else{
    if (b<c) printf("%lld\n",b);
    else printf("%lld\n",b);
  }
  //printf("%lld\n", words);
  //sprintf(output_file_name, "%s.output", pairs_file_name);
  //fin = fopen(pairs_file_name, "rb");
  //fout = fopen(output_file_name, "w");
  //if (f == NULL){
  //    printf("Pairs file not found\n");
  //    return -1;
  //}
  //for (a = 0; a < 8; ++ a)
  //{
    //fscanf(fin, "%s%c%s", st1, &ch, st2);
    for (b = 0; b < words; ++ b) if (!strcmp(&vocab[b * max_w], st1)) break;
    if (b == words) b = -1;
    for (c = 0; c < words; ++ c) if (!strcmp(&vocab[c * max_w], st2)) break;
    if (c == words) c = -1;
    if (b == -1 || c == -1){
      dist=-1.0f;
      printf("%.6f\n",dist);
    }
    else
    {
      for (d = 0; d < size; ++d)
      {
        vec1[d] = M[d + b * size];
        vec2[d] = M[d + c * size];
      }
      len = 0;
      for (d = 0; d < size; ++ d) len += vec1[d] * vec1[d];
      len = sqrt(len);
      for (d = 0; d < size; ++ d) vec1[d] /= len;
      len = 0;
      for (d = 0; d < size; ++ d) len += vec2[d] * vec2[d];
      len = sqrt(len);
      for (d = 0; d < size; ++ d) vec2[d] /= len;
      dist = 0;
      for (d = 0; d < size; ++ d) dist += vec1[d] * vec2[d];
      printf("%.6f\n",dist);
    //}
  }
  //fclose(fin);
  //fclose(fout);
  return 0;
}
