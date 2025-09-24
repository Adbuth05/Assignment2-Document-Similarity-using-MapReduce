package com.example.controller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import com.example.DocSimMapper;
import com.example.DocSimReducer;

import java.io.IOException;

public class DocSimDriver {
    public static void main(String[] inputArgs) throws IOException, InterruptedException, ClassNotFoundException {
        // Set up Hadoop configuration
        Configuration conf = new Configuration();
        Job similarityJob = Job.getInstance(conf, "Document Jaccard Similarity");

        // Configure job classes
        similarityJob.setJarByClass(DocSimDriver.class);
        similarityJob.setMapperClass(DocSimMapper.class);
        similarityJob.setReducerClass(DocSimReducer.class);

        // Define key/value output formats
        similarityJob.setOutputKeyClass(Text.class);
        similarityJob.setOutputValueClass(Text.class);

        // Set input and output paths
        FileInputFormat.addInputPath(similarityJob, new Path(inputArgs[0]));
        FileOutputFormat.setOutputPath(similarityJob, new Path(inputArgs[1]));

        // Run the job
        System.exit(similarityJob.waitForCompletion(true) ? 0 : 1);
    }
}
