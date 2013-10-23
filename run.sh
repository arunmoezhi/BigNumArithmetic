export BC_LINE_LENGTH=100000000
export CLASSPATH=$PWD
echo "BigNum  Runtime:"
time java BigNum
echo ""
echo "unix bc Runtime:"
time bc inputBc.txt | cat > outputBc.txt
echo ""
diff output.txt outputBc.txt
