# Assignment 2: Document Similarity using MapReduce

**Name:*Adbuth Kumar Kasturi* 

**Student ID:*801424745* 

## Approach and Implementation

### Mapper Design
The Mapper takes each line of the input dataset, which is in the form:

```bash
DocumentID <document text>
```

Input Key-Value Pair:
    1. Key: Byte offset of the line in the input file (ignored in our logic).
    2. Value: The entire line of text (document ID + content).

Processing Logic:
    1. The line is split into two parts: the Document ID and the document text.
    2. The document text is tokenized into individual words.
    3. For each word, the Mapper emits a key-value pair (word, DocumentID).

Output Key-Value Pair:

``` bash
(word, documentID)
```

Role in Overall Problem:
This design ensures that all documents containing the same word are grouped together in the Reducer phase, which allows us to build document pairs and later compute their Jaccard similarity.

### Reducer Design
Input Key-Value Pair:
    1.Key: A single word.
    2.Value: A list of document IDs in which that word appears.
    
Logic / Processing Steps:
    1.The reducer takes a word and the list of document IDs containing that word.
    2.It generates all possible pairs of documents from that list.
    3.Example: if the word “Hadoop” appears in Document1, Document2, and Document3, then the reducer forms pairs: (Document1, Document2), (Document1, Document3), (Document2, Document3).
    4.For each pair, it increments a counter that represents how many words those two documents have in common (this builds the intersection count).
    5.Separately, the reducer also tracks the union size for each document pair, which is the total number of distinct words across both documents.
    6.After processing all words, it calculates the Jaccard similarity for each document pair.  
Output Key-Value Pir:
```bash
(DocumentA, DocumentB) -> Jaccard similarity score
```
Jaccard similarity formula:

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
These files are stored in HDFS and read by the MapReduce framework line by line.

2. Mapper Phase

Each line is split into a document ID and its text.
The text is tokenized into words.
The Mapper emits (word, documentID) pairs.

3.Shuffle and Sort Phase

Hadoop automatically groups all the values (document IDs) that share the same word.

This means every word is now associated with the list of documents where it appears.

Reducer Phase

For each word, the reducer takes the list of documents.

It generates all possible pairs of documents that share that word.

The reducer accumulates counts of common words and calculates the Jaccard similarity:

J(A, B) = |A ∩ B| / |A ∪ B|


The output is written as (DocumentA, DocumentB) → similarity score.

Final Output

The results are stored in the output directory of HDFS.

They can be viewed with hadoop fs -cat /output/* or copied back to the local system for analysis.
---

## Setup and Execution

### ` Note: The below commands are the ones used for the Hands-on. You need to edit these commands appropriately towards your Assignment to avoid errors. `

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/WordCountUsingHadoop-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp shared-folder/input/data/input.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/WordCountUsingHadoop-0.0.1-SNAPSHOT.jar com.example.controller.Controller /input/data/input.txt /output1
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions

[Describe any challenges you faced during this assignment. This could be related to the algorithm design (e.g., how to generate pairs), implementation details (e.g., data structures, debugging in Hadoop), or environmental issues. Explain how you overcame these challenges.]

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
