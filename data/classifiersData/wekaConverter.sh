#!/usr/bin/sh
java -classpath weka.jar weka.core.converters.TextDirectoryLoader -dir "$1" -charset "UTF-8" >  "$1.arff"
java -classpath weka.jar weka.core.converters.TextDirectoryLoader -dir "$2" -charset "UTF-8" >  "$2.arff"
java -classpath weka.jar  weka.filters.unsupervised.attribute.NominalToString -C 1 -b -i $1.arff  -o $1.tmp.arff -r $2.arff -s $2.tmp.arff
mv $1.tmp.arff $1.arff
mv $2.tmp.arff $2.arff
java -classpath weka.jar  weka.filters.unsupervised.attribute.StringToWordVector -R 1 -b -i $1.arff  -o $1.tmp.arff -r $2.arff -s $2.tmp.arff
mv $1.tmp.arff $1.arff
mv $2.tmp.arff $2.arff
java -classpath weka.jar  weka.filters.unsupervised.attribute.Reorder -R 2-last,1 -b -i $1.arff  -o  $1.tmp.arff -r  $2.arff -s $2.tmp.arff
mv $1.tmp.arff $1.arff
mv $2.tmp.arff $2.arff
java -classpath weka.jar  weka.filters.unsupervised.attribute.ClassAssigner -C last  -b -i $1.arff  -o  $1.tmp.arff -r  $2.arff -s $2.tmp.arff
mv $1.tmp.arff $1.arff
mv $2.tmp.arff $2.arff
#java -classpath weka.jar weka.filters.supervised.attribute.AttributeSelection -E "weka.attributeSelection.ChiSquaredAttributeEval" -S "weka.attributeSelection.Ranker -T 0 -N -1" -b -i $1.arff  -o $1Final.arff -r $2.arff -s $2Final.arff
java -classpath weka.jar  weka.filters.unsupervised.attribute.RandomProjection -N 400 -R 42 -D Sparse1 -c last -b -i $1.arff  -o $1Proj.arff -r $2.arff -s $2Proj.arff
