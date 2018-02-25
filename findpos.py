import nltk
from nltk import pos_tag, word_tokenize
import sys

input_text=str(sys.argv[1])
out= nltk.pos_tag(word_tokenize(input_text))
for i in range(len(out)): 
	print out[i][1]
