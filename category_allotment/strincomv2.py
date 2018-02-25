from sklearn.feature_extraction.text import TfidfVectorizer
from glob import iglob
import re
import os
import sys
import operator
from difflib import SequenceMatcher 
import nltk
from nltk.stem import PorterStemmer
from nltk.stem import WordNetLemmatizer
wnl = WordNetLemmatizer()
from nltk.tokenize import sent_tokenize, word_tokenize
##POS TAG CODE####
def findpos(text):
	tokenized=nltk.word_tokenize(text)
	return nltk.pos_tag(text)[0][1]

##STOPWORD & LEMMATIZATION
def removegarbage(text):
    # Replace one or more non-word (non-alphanumeric) chars with a space
    text = re.sub(r'\W+', ' ', text)
    text = text.replace('_', ' ')
    text = text.lower()
    #print text	
    line = ""	
    for wordd in text.split(" "):
	#print wordd
    	new = wnl.lemmatize(wordd)	
    	line=line+" " +str(new)
    #print line
    return line

####TF-IDF#######
corpus = []
with open('red.txt') as sanf:
    for video in sanf:
    	corpus.append(removegarbage(video))

vectorizer = TfidfVectorizer(stop_words='english')
X = vectorizer.fit_transform(corpus)
idf = vectorizer.idf_
abc=dict(zip(vectorizer.get_feature_names(), idf))

corpus = []
with open('WN_final.txt') as wordnet:
    for videos in wordnet:
    	corpus.append(removegarbage(videos))

vectorizer = TfidfVectorizer(stop_words='english')
X = vectorizer.fit_transform(corpus)
idf = vectorizer.idf_
wordnet_tf_idf=dict(zip(vectorizer.get_feature_names(), idf))

#print "******************W_n Tf_Idf************************"
#sorted_x = sorted(wordnet_tf_idf.items(), key=operator.itemgetter(1),reverse=True)
#for item in sorted_x:
#	print item , ","

#print "******************Queries Tf_Idf************************"
#sorted_x = sorted(abc.items(), key=operator.itemgetter(1),reverse=True)
#for item in sorted_x:
#	print item , ","



def similar(a, b):
	return SequenceMatcher(None, a, b).get_matching_blocks()
##take initial few words from a Long string###
def first_words(input, words):
    for i in range(0, len(input)):
        # Count spaces in the string.
        if input[i] == ' ':
            words -= 1
        if words == 0:
            # Return the slice up to this point.
            return input[0:i]
    return ""



###STRING MATCH#####
d={}
with open('red.txt') as sanf:
    for video in sanf:
	video=removegarbage(video)
	lemmatized=[]
	print video,": ", #first_words(video,70)
	document_1_text=video.lower()
	document_1_words = document_1_text.split()
	#print "DOX:" ,document_1_words
	for value in document_1_words:
		#print value
		new=wnl.lemmatize(value)
		lemmatized.append(new)
	#print lemmatized	
	with open('WN_final.txt') as f:
    		for initial in f:
			line=removegarbage(initial)
			line=first_words(line, 70)
			#print line
			lemmatizedwn=[]
			#line = nline.split("\n")
			dic= line.replace("\n", "");
			#print line, similar(sys.argv[1],line)
			document_2_text=line.lower()
			
			document_2_words = document_2_text.split()
			for wnvalue in document_2_words:
				#print value
				news=wnl.lemmatize(wnvalue)
				lemmatizedwn.append(news)
			common = set(lemmatized).intersection( set(lemmatizedwn) )
			val=0
			valwn=0
			pos=0
			for them in common:
			#	print them
				#print k.find(them)
				pos=pos+(len(document_2_text)-document_2_text.find(them))
				if them in abc:
					val=val+float(abc[them])
				if them in wordnet_tf_idf:
					valwn=valwn+float(wordnet_tf_idf[them])
			#print val+valwn,"Tf_Idfoverall"
			#print pos,"positionfromright(thelessermorefavourable)"
			final_weight=5*val+5*valwn+1*len(common)
			d[initial]=final_weight
			
			
			#d[document_2_text]=len(common)
		newA = dict(sorted(d.items(), key=operator.itemgetter(1),reverse=True)[:10])
		hell = sorted(d.items(), key=operator.itemgetter(1),reverse=True)
		#print "Answer:-",
		for item in hell[:1]:
			#print item
			node1=item[0].rsplit('/', 1)[-1]
			print first_words(node1,1), "," ,
		        node2=item[0].rsplit('/', 1)[-2]
			print node2.rsplit('/',1)[-1]
		   
