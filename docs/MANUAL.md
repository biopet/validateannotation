# Manual

ValidateAnnotation requires the refflatfile, a list of gtf files and a reference genome to check the annotation.

Example:

```bash
java -jar ValidateAnnotation-version.jar \
-r refflatFile \
-g gtfFile1 \
-g gtfFile2 \ 
-g gtfFile3 \
-R reference.fasta
```