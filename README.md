# rerunner-jupiter
[![Build Status](https://travis-ci.org/artsok/rerunner-jupiter.svg?branch=master)](https://travis-ci.org/artsok/rerunner-jupiter)

### Extension for Junit 5. Re-run failed JUnit tests immediately.
### Very useful when you UI/API tests don't stable.

### HOW TO USE

```xml
<dependency>
    <groupId>io.github.artsok</groupId>
    <artifactId>rerunner-jupiter</artifactId>
    <version>1.0.0</version>
</dependency>
```


```java
           /** 
            * Repeated three times if test failed.
            * By default Exception.class will be handled in test
            */
           @RepeatedIfExceptionsTest(repeats = 3)
           void reRunTest() throws IOException {
               throw new IOException("Error in Test");
           }
       
       
           /**
            * Repeated two times if test failed. Set IOException.class that will be handled in test
            * @throws IOException - error occurred
            */
           @RepeatedIfExceptionsTest(repeats = 2, exceptions = IOException.class)
           void reRunTest2() throws IOException {
               throw new IOException("Exception in I/O operation");
           }
       
       
           /**
            * Repeated ten times if test failed. Set IOException.class that will be handled in test
            * Set formatter for test. Like behavior as at {@link org.junit.jupiter.api.RepeatedTest}
            * @throws IOException - error occurred
            */
           @RepeatedIfExceptionsTest(repeats = 10, exceptions = IOException.class, 
           name = "Rerun failed test. Attempt {currentRepetition} of {totalRepetitions}")
           void reRunTest3() throws IOException {
               throw new IOException("Exception in I/O operation");
           }
```
