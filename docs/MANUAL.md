# Manual

ValidateAnnotation requires the refflatfile and a reference genome to check the annotation. A list of gtf files can be optionally used for checking as well.

Example:

```bash
java -jar ValidateAnnotation-version.jar \
-r refflatFile \
-g gtfFile1 \
-g gtfFile2 \ 
-g gtfFile3 \
-R reference.fasta
```
