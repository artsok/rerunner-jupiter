# rerunner-jupiter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.artsok/rerunner-jupiter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.artsok/rerunner-jupiter)
[![Build Status](https://travis-ci.org/artsok/rerunner-jupiter.svg?branch=2.0.1)](https://travis-ci.org/artsok/rerunner-jupiter)
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
    <version>2.0.1</version>
    <scope>test</scope>
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
           
          /**
           * By default total repeats = 1 and minimum success = 1.
           * If the test failed by this way start to repeat it by one time with one minimum success.
           *
           * This example without exceptions.
           */
          @ParameterizedRepeatedIfExceptionsTest
          @ValueSource(ints = {14, 15, 100, -10})
          void successfulParameterizedTest(int argument) {
              System.out.println(argument);
          }
      
          /**
           * By default total repeats = 1 and minimum success = 1.
           * If the test failed by this way start to repeat it by one time with one minimum success.
           * This example with display name but without exceptions
           */
          @DisplayName("Example of parameterized repeated without exception")
          @ParameterizedRepeatedIfExceptionsTest
          @ValueSource(ints = {1, 2, 3, 1001})
          void successfulParameterizedTestWithDisplayName(int argument) {
              System.out.println(argument);
          }
      
          /**
           * By default total repeats = 1 and minimum success = 1.
           * If the test failed by this way start to repeat it by one time with one minimum success.
           *
           * This example with display name but with exception. Exception depends on random number generation.
           */
          @DisplayName("Example of parameterized repeated with exception")
          @ParameterizedRepeatedIfExceptionsTest
          @ValueSource(strings = {"Hi", "Hello", "Bonjour", "Privet"})
          void errorParameterizedTestWithDisplayName(String argument) {
              if (random.nextInt() % 2 == 0) {
                  throw new RuntimeException("Exception " + argument);
              }
          }
      
          /**
           * By default total repeats = 1 and minimum success = 1.
           * If the test failed by this way start to repeat it by one time with one minimum success.
           *
           * This example with display name, repeated display name, 10 repeats and 2 minimum success with exceptions.
           * Exception depends on random number generation.
           */
          @ParameterizedRepeatedIfExceptionsTest(name = "Argument was {0}",
                  repeatedName = " (Repeat {currentRepetition} of {totalRepetitions})",
                  repeats = 10, exceptions = RuntimeException.class, minSuccess = 2)
          @ValueSource(ints = {4, 5, 6, 7})
          void errorParameterizedTestWithDisplayNameAndRepeatedName(int argument) {
              if (random.nextInt() % 2 == 0) {
                  throw new RuntimeException("Exception in Test " + argument);
              }
          }
      
          /**
           * By default total repeats = 1 and minimum success = 1.
           * If the test failed by this way start to repeat it by one time with one minimum success.
           *
           * This example with display name, implicitly repeated display name, 4 repeats and 2 minimum success with exceptions.
           * Exception depends on random number generation. Also use {@link MethodSource}
           */
          @DisplayName("Display name of container")
          @ParameterizedRepeatedIfExceptionsTest(name = "Year {0} is a leap year.",
                  repeats = 4, exceptions = RuntimeException.class, minSuccess = 2)
          @MethodSource("stringIntAndListProvider")
          void errorParameterizedTestWithMultiArgMethodSource(String str, int num, List<String> list)  {
              assertEquals(5, str.length());
              assertTrue(num >= 1 && num <= 2);
              assertEquals(2, list.size());
              if (random.nextInt() % 2 == 0) {
                  throw new RuntimeException("Exception in Test");
              }
          }
      
          static Stream<Arguments> stringIntAndListProvider() {
              return Stream.of(
                      arguments("apple", 1, Arrays.asList("a", "b")),
                      arguments("lemon", 2, Arrays.asList("x", "y"))
              );
          }
          
          
          /**
          *  it's often caused by some infrastructure problems: network congestion, garbage collection etc. 
          *  These problems usually pass after some time. Use suspend option
          */
          @RepeatedIfExceptionsTest(repeats = 3, exceptions = IOException.class, suspend = 5000L)
              void reRunTestWithSuspendOption() throws IOException {
                  throw new IOException("Exception in I/O operation");
              }
          
          
              /**
               * Example with suspend option for Parameterized Test
               * It matters, when you get some infrastructure problems and you want to run your tests through timeout.
               *
               * Set break to 5 seconds. If exception appeared for any arguments, repeating extension would runs tests with break.
               * If one result failed and other passed, does not matter we would wait 5 seconds throught each arguments of the repeated tests.
               *
               */
              @DisplayName("Example of parameterized repeated with exception")
              @ParameterizedRepeatedIfExceptionsTest(suspend = 5000L, minSuccess = 2, repeats = 3)
              @ValueSource(strings = {"Hi", "Hello", "Bonjour", "Privet"})
              void errorParameterizedTestWithSuspendOption(String argument) {
                  if (random.nextInt() % 2 == 0) {
                      throw new RuntimeException(argument);
                  }
              }
```
More examples you can find [here].


## GitHub Star
Push to the [star] if you like this JUnit 5 Extension. By this way, I will get feedback from you!


[here]: https://github.com/artsok/rerunner-jupiter/blob/master/src/test/java/io/github/artsok/ReRunnerTest.java
[star]: https://github.com/artsok/rerunner-jupiter/stargazers
