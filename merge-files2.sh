pd=data/
dd=result3																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																						.txt
find $pd -type d | rename 's/ /_/g' 
find $pd -type f | rename 's/ /#/g' 
filenames=$(find $pd -type f)

for item in $filenames
do 
	#content=$(unzip -p $item sources.xml | sed -e 's/<\/w:p>/\n/g; s/<[^>]\{1,\}>//g; s/[^[:print:]\n]\{1,\}//g')	
	#echo "cat $item" | tr -d '[:punct:]' >> "$dd"
	value=`cat $item` 
	echo "$value" | tr -d '[:punct:]'
	
done

#to run it, use ./merge-files.sh >> result2.txt

