# Introduction for KeyWord Identification:

When we write a search query which is a short sentence, we want to know what the
keywords/keyphrases are in that sentence which will then be used to get the search results for the
corrresponding query. For getting better search results, we need to segment the text in the most
meaningful way (for the best identification of keywords/keyphrases) in the given search query.
For example, if the search query is 'New Delhi in India', then its text chunks are 'new delhi' and
'india' and not 'new', 'delhi' and 'india'.

During my project, I worked on finding the best segmentation of the text in the search query.
However, in this, the traditional approaches (like TF*IDF, etc.) do not work properly as a short
sentence does not have enough statistics. Also, the search query may not properly follow the syntax
of the written language.

So I used semantic relation and cooccurence frequency between the phrases (and also POS tags) as
parameters to obtain the best segmentation of the search query. The detailed algorithm is listed
below.

# Thoughts behind the algorithm:

If the phrases are highly semantically related to each other, then there is a higher chance for those
phrases to exist separately and not as a single keyphrase. So we try to find that combination of
keyphrases such that the semantic relation between those keyphrases overall is maximum.
However, we also need to account for the cooccurence frequency between those phrases because
the algorithm used by the word2vec library (which is used for getting the semantic relation) uses
CBOW algorithm and thus sometimes higher cooccurence frequency leads to higher semantic
relation which results in the phrases to get separated in my algorithm when instead they should
occur together (as they have higher cooccurence frequency and thus higher probability to occur
together). So the final relation is decided by a combination of them in which the semantic relation
is the major parameter and cooccurence frequency is used to increase or decrease its impact
depending on how much less or more the phrases cooccur in the training data. The POS tags are
also considered for the case when there are two words.

# Algorithm:
We use the corpus given by the expert and also the 'text8' corpus (that comes along with the
word2vec library) and train the data using the word2vec library. This library is used to get semantic
relation and coccurrence between any two phrases.
Firstly, the expert data is merged into a single file and the data is cleaned. Then the stopwords are
removed. I wrote a script that calculates the distance between the two phrases which helps us give
the semantic relation between the words and also caculates the frequency of the cooccurnce of the
combination of the two phrases which helps me to get the cooccurence frequency of the twophrases.
Then its possible contiguous subsequence phrases are found. Then we store the semantic relation,
cooccurence relation and final relation between these obtained phrases in a graph.
After this, the maximum weighted edge is found and then it removes all nodes that are disconnected
with the picked nodes corresponding to the max weighted edge (this is done to remove words like,
for example, 'natural' to come again if 'natural resources' has already been selected as the keyword).
At the same time, it removes all edges that are linked to the deleted nodes. This process is repeated
until no edges can be selected. The nodes that are finally picked are the keyphrases of the search
query.

Special case of two words: In this case, my algorithm always prints them as separate words
(in the algorithm, the nodes of the max weighted edge are taken, so there will be atleast 2
phrases) and also the semantic relation is not a reliable parameter. So the combination of
POS tags and cooccurence frequency is used to make the algorithm work properly for this
case. For example, if they have a higher coccurence frequency or their POS tags are of
'adjective noun' or 'noun noun', they are more likely to occur together.

To understand the algorithm in a better manner, please have a look at my code (major part of the code is in ki.java).

# Results:

In this, there are two things that should be kept in mind.
1. Evaluating the text segmentation is slightly dependent on how a person thinks they should be
segmented (for example, 'natural resources in india' should be chunked as 'natural resources' and
'india' by some person but another may argue that it should be chunked as 'natural', 'resources' and
'india' because the word 'natural' can be asociated witha lot of other things other than the word
'resources').
2. The results are heavily dependent on the data (for example, if we take a data in which 'united
states' does not appear and rather phrases like 'united india' and 'united team' occur much, then this
will provide united and states as different chunks and not as a single chunk).

Few samples:
new delhi india
new delhi ( JJ NN )
india ( NN )

appalachicola river in florida
appalachicola ( NN )
river ( NN )
florida ( NN )

rainwater harvesting
rainwater harvesting ( NN VBG )

bee wax and royal jelly
bee ( NN )
wax ( NN )
royal jelly ( NN RB )

bija yatra
bija yatra ( NN NN )

iron ores of the kallakurchi
iron ( NN )
ores ( NNS )
kallakurchi ( NN )

self reliance in food
self reliance ( NN NN )
food ( NN )

Gene Campaign
gene campaign ( NN NN )

prices of chemical pesticide and fertilizers
prices ( NNS )
chemical ( NN )
pesticide ( NN )
fertilizers ( NNS )

valedictory address by Prasant Mohanty
valedictory ( NN )
address ( NN )
prasant mohanty ( JJ NN )

budget allocation for revival of agriculture
budget ( NN )
allocation ( NN )
revival ( NN )
agriculture ( NN )

The algorithm produces quite good results . But this is a kind of a problem in which there is still a
huge potential to improve the results which can be done by taking more data and/or taking user
feedback and/or identifying and considering more factors and their combinations.
