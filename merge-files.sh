pd=sandeep/
dd=result.txt
dd2=output.txt
find $pd -type d | rename 's/ /_/g' 
find $pd -type f | rename 's/ /_/g' 
filenames=$(find $pd -type f)

for item in $filenames
do 
	content=$(unzip -p $item word/document.xml | sed -e 's/<\/w:p>/\n/g; s/<[^>]\{1,\}>//g; s/[^[:print:]\n]\{1,\}//g')
	echo "$content" >> "$dd"
done

tr '\n' ' ' < $dd   | sed 's/\ $//g' > $dd2
tr -s " " < $dd2 > $dd
rm -f $dd2
