package com.example;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class DocSimMapper extends Mapper<Object, Text, Text, Text> {
    @Override
    public void map(Object key, Text line, Context ctx) throws IOException, InterruptedException {
        // Extract the file name for identifying the document
        String documentName = ((FileSplit) ctx.getInputSplit()).getPath().getName();

        // Collect unique words
        HashSet<String> uniqueTokens = new HashSet<>();
        StringTokenizer tokenStream = new StringTokenizer(line.toString());
        while (tokenStream.hasMoreTokens()) {
            uniqueTokens.add(tokenStream.nextToken().toLowerCase());
        }

        // Emit document name and its unique words
        ctx.write(new Text(documentName), new Text(String.join(",", uniqueTokens)));
    }
}
