# rerunner-jupiter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.artsok/rerunner-jupiter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.artsok/rerunner-jupiter)
[![Build Status](https://travis-ci.org/artsok/rerunner-jupiter.svg?branch=master)](https://travis-ci.org/artsok/rerunner-jupiter)
![badge-jdk-8](https://img.shields.io/badge/jdk-8-yellow.svg "JDK-8")
[![License badge](https://img.shields.io/badge/license-Apache2-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)

*rerunner-jupiter* is a extension for Junit 5. 
Re-run failed JUnit-Jupiter tests immediately. Very useful when you UI/API tests don't stable. 
This library is open source, released under the terms of [Apache 2.0 License].

## How To Use

In order to include *rerunner-jupiter* in a Maven project, first add the following dependency to your `pom.xml` (Java 8 required):

```xml
<dependency>
    <groupId>io.github.artsok</groupId>
    <artifactId>rerunner-jupiter</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Examples
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
           
           /**
           *  Repeated three times if selenium test failed. Use selenium-jupiter extension.
           */
           @RepeatedIfExceptionsTest(repeats = 3, exceptions = NoSuchElementException.class)
           void testWithChrome(ChromeDriver chrome)  {
                chrome.get("http://yandex.ru");
                chrome.findElement(By.xpath("//span[@id='authors']"));
           }
           
           /**
           * Repeated 100 times with minimum success four times, then disabled all remaining repeats.
           * See image below how it works. Default exception is Exception.class
           */
           @DisplayName("Test Case Name")
           @RepeatedIfExceptionsTest(repeats = 100, minSuccess = 4)
           void reRunTest4() {
                if(random.nextInt() % 2 == 0) {
                    throw new RuntimeException("Error in Test");
                }
           }
           
```
