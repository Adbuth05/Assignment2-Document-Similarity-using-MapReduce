# Assignment 2: Document Similarity using MapReduce

**Name:*Adbuth Kumar Kasturi* 

**Student ID:*801424745* 

## Approach and Implementation

### Mapper Design
The Mapper takes each line of the input dataset, which is in the form:

```bash
DocumentID <document text>
```
- Point one Input Key-Value Pair:
    - Subpoint Key: Byte offset of the line in the input file (ignored in our logic).
    - Subpoint Value: The entire line of text (document ID + content).

- Point two Processing Logic:
    - Subpoint The line is split into two parts: the Document ID and the document text.
    - Subpoint The document text is tokenized into individual words.
    - Subpoint For each word, the Mapper emits a key-value pair (word, DocumentID).

-Point One Output Key-Value Pair:

``` bash
(word, documentID)
```

Role in Overall Problem:
This design ensures that all documents containing the same word are grouped together in the Reducer phase, which allows us to build document pairs and later compute their Jaccard similarity.

### Reducer Design
- Point one Input Key-Value Pair:
    - Subpoint Key: A single word.
    - Subpoint Value: A list of document IDs in which that word appears.
    
- Point two Logic / Processing Steps:
    - SubpointThe reducer takes a word and the list of document IDs containing that word.
    - SubpointIt generates all possible pairs of documents from that list.
    - SubpointExample: if the word “Hadoop” appears in Document1, Document2, and Document3, then the reducer forms pairs: (Document1, Document2), (Document1, Document3), (Document2, Document3).
    - SubpointFor each pair, it increments a counter that represents how many words those two documents have in common (this builds the intersection count).
    - SubpointSeparately, the reducer also tracks the union size for each document pair, which is the total number of distinct words across both documents.
    - SubpointAfter processing all words, it calculates the Jaccard similarity for each document pair.  

- Point three Output Key-Value Pir:
```bash
(DocumentA, DocumentB) -> Jaccard similarity score
```
- Point four Jaccard similarity formula:

*J(A, B) = |A ∩ B| / |A ∪ B|*
where |A ∩ B| is the number of shared words, and |A ∪ B| is the total unique words.

How this helps in solving the problem:
By systematically comparing documents word by word, the reducer produces the final similarity scores between all document pairs. These scores can be used to identify how close documents are in terms of content.

### Overall Data Flow

1. Input Stage

- Point one The input files are provided in the format:
``` bash
DocumentID <document text>
```
- Point two These files are stored in HDFS and read by the MapReduce framework line by line.

2. Mapper Phase

- Point one Each line is split into a document ID and its text.
- Point two The text is tokenized into words.
- Point three The Mapper emits (word, documentID) pairs.

3.Shuffle and Sort Phase

- Point one Hadoop automatically groups all the values (document IDs) that share the same word.

- Point two This means every word is now associated with the list of documents where it appears.

4. Reducer Phase

- Point one For each word, the reducer takes the list of documents.

- Point two It generates all possible pairs of documents that share that word.

- Point three The reducer accumulates counts of common words and calculates the Jaccard similarity:

J(A, B) = |A ∩ B| / |A ∪ B|

- Point four The output is written as (DocumentA, DocumentB) → similarity score.

5. Final Output
- Point One The results are stored in the output directory of HDFS.
- Point twoThey can be viewed with hadoop fs -cat /output/* or copied back to the local system for analysis.
---

## Setup and Execution

### ` Note: The below commands are the ones used for the Hands-on. You need to edit these commands appropriately towards your Assignment to avoid errors. `

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build JAR**

Build the code using Maven:

```bash
mvn clean install
```

### 4. **Copy JAR to Hadoop Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp shared_folder/input_files/ resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Upload data to HDFS**

```bash
hadoop fs -mkdir -p /input/dataset
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/dataset/datasets /output
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
   docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions
- Point one Challenge: Handling large datasets with many overlapping words.
- Point two Solution: Optimized reducer logic to compute intersections efficiently.

- Point three Challenge: Setting up multi-node Hadoop with Docker.
- Point four Solution: Used docker-compose.yml and hadoop.env to configure namenode, datanodes, resourcemanager, and historyserver.

- Point five Challenge: Output overwriting when re-running jobs.
- Point six Solution: Used unique output folder names for each run or deleted old output before execution.

---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)
```bash
(ds2.txt, ds1.txt) -> 0.03
(ds3.txt, ds1.txt) -> 0.01
(ds3.txt, ds2.txt) -> 0.29
```
