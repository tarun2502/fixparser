# Getting Started with FIX Parser #

```
        InputStream inputStream = null;

        try {
            // initialize an input stream
            inputStream = new BufferedInputStream(new FileInputStream(file));

            // initialize the parser
            SimpleFixParser parser = new SimpleFixParser(inputStream);

            // main loop
            while (true) {

                // parse a FIX Message
                SimpleFixMessage msg = parser.readFixMessage();

                if (null == msg) {
                    break; // exit the loop, end of the stream reached
                }

                // process FIX message
                // System.out.println(msg);
            }
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
        } finally {
            // close the stream if it is not null
            IoUtils.closeSilently(inputStream);
        }
```

# A Wroking Sample #
http://fixparser.googlecode.com/svn/trunk/src/main/java/net/java/fixparser/samples/Sample.java