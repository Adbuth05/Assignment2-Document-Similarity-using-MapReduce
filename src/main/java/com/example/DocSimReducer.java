package com.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DocSimReducer extends Reducer<Text, Text, Text, Text> {
    private final Map<String, Set<String>> docWordsMap = new HashMap<>();

    @Override
    public void reduce(Text docId, Iterable<Text> terms, Context ctx) throws IOException, InterruptedException {
        Set<String> currentDocWords = new HashSet<>();
        for (Text termList : terms) {
            currentDocWords.addAll(Arrays.asList(termList.toString().split(",")));
        }

        docWordsMap.put(docId.toString(), currentDocWords);

        for (Map.Entry<String, Set<String>> storedEntry : docWordsMap.entrySet()) {
            String otherDocId = storedEntry.getKey();
            Set<String> otherDocWords = storedEntry.getValue();

            if (otherDocId.equals(docId.toString())) {
                continue;
            }

            // Compute Jaccard similarity
            Set<String> common = new HashSet<>(otherDocWords);
            common.retainAll(currentDocWords);

            Set<String> combined = new HashSet<>(otherDocWords);
            combined.addAll(currentDocWords);

            double jaccardScore = (double) common.size() / combined.size();
            double roundedScore = Math.round(jaccardScore * 100.0) / 100.0;

            if (roundedScore >= 0.01) {
                String docA = docId.toString();
                String docB = otherDocId;
                ctx.write(new Text("(" + docA + ", " + docB + ")"), new Text("-> " + roundedScore));
            }
        }
    }
}
