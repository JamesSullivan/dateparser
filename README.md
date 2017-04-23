# dateparser #

Welcome to dateparser!

A simple English language explicit date parser. This parser only supports parsing of complete explicit dates from 1901 to 2099. Dates without the century (two digit years) default to 20th century if greater than or equal to 50 or to the 21st century if less than. Although most date formats are clear, certain date formats can be ambiguous and the locale must be set or else by default priority will be given to US date format interpretations.

It does not parse time (9:00), relaxed dates (Oct 1st without the year), relative dates (the day before next Thursday), or date alternatives (next Mon or Tuesday). If you have those or similar requirements I recommend trying [Natty](http://natty.joestelmach.com/) or [datr.scala](https://github.com/platzhaltr/datr.scala).


## You may want to use this date parser if you need to...
* keep false positives to a minimum.

* be able to handle ambiguous English date formats of different regions. [Date format by country](https://en.wikipedia.org/wiki/Date_format_by_country)

* easily modify the code yourself to handle special cases (the code is only around a page long)


## Usage

```scala
import asia.solutions.dateparser._

val parser = new EnglishParser()
val dateResults: Seq[DateResult] = parser.dateResults("the date of Sept. 10, 1992, unless otherwise noted")

for (dr <- dateResults) {
    println("LocalDate: " + dr.localDate)
    // LocalDate: 1992-09-10

    println("Date text: " + dr.dateRawText)
    // Date text: Sept. 10, 1992

    println("Date text starts at character position: " + dr.index)
    // Date text starts at character position: 26
}
```

See the examples in the [EnglishParserSpec.scala](https://github.com/JamesSullivan/dateparser/blob/master/src/test/scala/asia/solutions/dateparser/EnglishParserSpec.scala) test file for examples on additional functionality


## Supported formats
* on 2014-08-20

* 84/04/02

* on 1984/04/02

* 20/2/1980

* 2nd August 2015

* Aug 2nd 2015

* Sept. Third 2015

* on 3rd Sep 2015

* Ninth of October 1983

* 20140102


## Unsupported by default but easily enabled 
The following formats can lead to false positives so they must be specifically enabled by changing 

**22.04.96**  rare in English -- to support change line 92 to     val Divider = ("/" | "-" | ".") 

**04062005**  to naively support change line 92 to  val Divider = ("/" | "-").? 

a better change is to add a specific parser


## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.


## License ##

This code is open source software licensed under the
[MIT](https://opensource.org/licenses/MIT) license.


## Uses
[FastParse](http://lihaoyi.github.io/fastparse/)
