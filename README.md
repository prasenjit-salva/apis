# apis

## 1. Java api to support zipping all files (one level or multilevel) with or without root folder
### Required Maven files 
#### 1. 

  <dependency>
      <groupId>org.apache.ant</groupId>
      <artifactId>ant</artifactId>
      <version>1.10.0</version>
  </dependency>
####2. 

  <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-io</artifactId>
      <version>1.3.2</version>
  </dependency>
  
  Remove unzip()method code in order to not use the ant library.
