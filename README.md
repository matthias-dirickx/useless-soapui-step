# useless-soapui-step
Rudimentary Custom step implementation based on the HTTP Teststep

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
