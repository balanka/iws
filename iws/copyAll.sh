for f in /Users/iwsmac/Downloads/import/bankStatement/43719244/*.CSV; do 
    mv -- "$f" "${f%.CSV}.txt" 
done
