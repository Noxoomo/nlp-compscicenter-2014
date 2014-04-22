
#!/bin/sh
crf_learn  -c 1.0 template ru_corpus_learn  model 
crf_test -m model ru_corpus_test  > result
