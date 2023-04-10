package com.company;

import weka.core.*;
import weka.core.converters.CSVLoader;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveUseless;

import java.io.File;
import java.util.Enumeration;

public class Main {

    public static double calculateGiniIndex(Instances data, String attribute) throws Exception {

        // Find the attribute index
        int attributeIndex = data.attribute(attribute).index();

        // Filter out missing values
        ReplaceMissingValues replace = new ReplaceMissingValues();
        replace.setInputFormat(data);
        data = Filter.useFilter(data, replace);

        // Convert nominal attributes to numeric
        StringToNominal stringToNominal = new StringToNominal();
        stringToNominal.setInputFormat(data);
        data = Filter.useFilter(data, stringToNominal);

        // Convert numeric attributes to nominal
        NumericToNominal numericToNominal = new NumericToNominal();
        numericToNominal.setInputFormat(data);
        data = Filter.useFilter(data, numericToNominal);

        // Remove useless attributes
        RemoveUseless removeUseless = new RemoveUseless();
        removeUseless.setInputFormat(data);
        data = Filter.useFilter(data, removeUseless);

        // Remove the attribute we want to calculate the Gini index for
        Remove removeFilter = new Remove();
        removeFilter.setInputFormat(data);
        removeFilter.setAttributeIndices(String.valueOf(attributeIndex + 1));
        Instances dataWithoutAttribute = Filter.useFilter(data, removeFilter);

        // Calculate the Gini index
        String maxValue="";
        double maxP=0.0;
        double giniIndex = 1.0;
        Enumeration<Object> values = data.attribute(attribute).enumerateValues();
        while (values.hasMoreElements()) {
            String value = (String) values.nextElement();
            double p = 0.0;
            for (int i = 0; i < dataWithoutAttribute.numInstances(); i++) {
                Instance instance = dataWithoutAttribute.instance(i);
                if (instance.stringValue(attributeIndex).equals(value)) {
                    p += 1.0;
                }
            }
            p /=  data.numInstances();
            if(p>maxP){
                maxP=p;
                maxValue=value;
            }
            giniIndex -= p * p;
        }
        System.out.println("Valoarea cu cea mai mare proportie a atributului "+attribute+" este "+maxValue+" si apare in "+maxP+" din cazuri");
        return giniIndex;
    }

    public static void main(String[] args) throws Exception {

        CSVLoader loader=new CSVLoader();
        loader.setSource(new File("C:/Users/Bogdan/Downloads/test.csv"));
        loader.setNoHeaderRowPresent(false);

        Instances data=loader.getDataSet();

        String attribute="thal";

        double giniIndex=calculateGiniIndex(data,attribute);

        System.out.println(giniIndex);
    }
}
