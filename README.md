# useless-soapui-step
Rudimentary Custom step implementation based on the HTTP Teststep.

It should be fairly simple to convert this useless library to a useful one. Just change these two items:
- UselessClientSupport (add your useful conversion here)
- UselessDummyClient (add the client logic here and call it with the UselessClientSupport class - just a suggestion following the existing setup for the HTTP Test Step)

# How to install
- Build JAR (mvn compile install)
- Put JAR in $SOAPUI_HOME/bin/ext
- Put the content of 'factories' and 'actions' folder in the /src/main/resources/META-INF folder in the respective foldes in $SOAPUI_HOME/bin

Create the foldes if they don't exist.

SoapUI should pick up all the JARs in the bin/ext folder, and the definitions for the factories should be processed as well.

If you want to customize where your libraries are or if you like to add additional JARs to the classpath that for some reason can't be added to bin/ext check out this post:
https://fuckagile.wordpress.com/2015/12/03/soapui-external-libraries/

# Why oh why
When I would stumble onto this that would be exactly my question.
There is no sensible reason for this library to exist as it is presented here. Then what was the purpose ?

I had to work with a transactional system that stemmed from GCOS mainframe. To make available the mainframe connection to a wider variety of applications there was a JAVA library that could be used. The library has as inputs an IP and port combo and a message. The output is a message in byte[] format that you can consume.

By proxying the library through an app to capture traffic (Fiddler at the time) I came to realize that it would not be straight-forward to just send messages directly over HTTP. Because I didn't want to get caught up in creating my own octet-streams I sought another way: enter SoapUI Custom Test Steps!

As a template the HTTP Test Step was used because it closely resembles what I will actually be doing.

The input is text-based, and my output was (mostly) XML or HTML-formatted text (for some backward compatiblity reasons) as well as JSON. Ideal to be consumed by the SoapUI assertions framework I'd say!

Basically I send the message as text,
Then the library that is used to handle that message is addressed, and it returns the response.
It's put in the same place as the HTTP Response would have been stored. So as far as SoapUI's concerned it's just handling HTTP messages with all the infrastructure that goes with it.

# How it's put together
First off: This can be done simpler.
I chose to add all kind of elements because I wanted my own test step.
It would be better to add just a factory for a custom protocol in this case though. That would make for a way simpler implementation.


# The Chain of Things
It proved to be quite the endeavor.
The two core items:
- UselessTestStepPanelBuilderFactory.java
- UselessRequestStepFactory.java

These items build the Test Step and together they constitute what you experience as the Test Step in SoapUI.

The reason there's that many classes is largely because of two reasons:
1. I'm not a developer and I copy the way of working from the existing application
2. A set of private fields that can't be overridden ruling out the possibility of just extending and overriding the Step Name for example.

One thing I did not change at all. And that is the configuration. I reuse the HttpTestRequestConfig object without extension. This to disturb the schema the least amount possible. I don't want to corrupt existing projects.
