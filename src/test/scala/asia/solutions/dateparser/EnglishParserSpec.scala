/*
 * Copyright (c) 2017 asia.solutions
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package asia.solutions.dateparser

import java.time.LocalDate
import java.util.Locale
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EnglishParserSpec() extends AnyFlatSpec with Matchers {

  private def date(y: Int, m: Int, d: Int) = LocalDate.of(y, m, d)
  // A U.S. parser
  val parser = new EnglishParser()
  // A European parser
  val euroParser = new EnglishParser(new Locale("en", "GB"))

  val aussieParser = new EnglishParser(new Locale("en", "AU"))

  "An EnglishParser.find" should "return a LocalDate, string date and the position of the last char matched in the date " in {
    parser.find("This is a test of the date 2007/11/01.").get should be(
      (date(2007, 11, 1), "2007/11/01", 37)
    )
    parser.find("This 2007/11/01.").get should be((date(2007, 11, 1), "2007/11/01", 15))
  }

  it should "handle 'Year Month Day formats' " in {
    parser.find("1978-01-28").get should be((date(1978, 1, 28), "1978-01-28", 10))
    parser.find("79-1-2").get should be((date(1979, 1, 2), "79-1-2", 6))
    parser.find("12/12/12").get should be((date(2012, 12, 12), "12/12/12", 8))
    parser.find("2006-Jun-16").get should be((date(2006, 6, 16), "2006-Jun-16", 11))
    parser.find("2002-09-24+06:00").get should be((date(2002, 9, 24), "2002-09-24", 10))
  }

  it should "handle 'Month Day Year' formats " in {
    parser.find("sun, 11/21/2010").get should be((date(2010, 11, 21), "11/21/2010", 15))
    parser.find("2-3-10").get should be((date(2010, 2, 3), "2-3-10", 6))
    parser.find("oct. 1, 1980").get should be((date(1980, 10, 1), "oct. 1, 1980", 12))
    parser.find("OMB ... [57 FR 21215, May 19, 1992] PART 510— INFORMATION").get should be(
      (date(1992, 5, 19), "May 19, 1992", 34)
    )
  }

  it should "even when set to US locale resolve unambigous non-US formats " in {
    parser.find("Date of Issue	23-Feb-12").get should be((date(2012, 2, 23), "23-Feb-12", 23))
    parser.find("28-Feb-97").get should be((date(1997, 2, 28), "28-Feb-97", 9))
    parser.find("RESOLUTION No. 537, OF 17 JUNE 2015").get should be(
      (date(2015, 6, 17), "17 JUNE 2015", 35)
    )
    parser.find("First of October 1983").get should be(
      (date(1983, 10, 1), "First of October 1983", 21)
    )
  }

  it should "handle ambiguous 'Day Month Year' formats when set to Europe" in {
    euroParser.find("sun, 02/01/58").get should be((date(1958, 1, 2), "02/01/58", 13))
    euroParser.find("2-3-10").get should be((date(2010, 3, 2), "2-3-10", 6))
  }

  it should "not recognize '.' separated formats as leads to too many false positives and not that used in English" in {
    euroParser.find("22.04.96") should be(None)
  }

  it should "recognize strict straight YYYYMMDD numbers without separators" in {
    euroParser.find("20140607").get should be((date(2014, 6, 7), "20140607", 8))
  }

  it should "not recognize nonstrict straight numbers without separators as it occassionally leads to false positives" in {
    euroParser.find("03042012") should be(None)
    euroParser.find("201234") should be(None)
  }

  it should "handle 'Sept.' and trailing commas in \"the date of Sept. 10, 1992, unless otherwise noted\"" in {
    parser.find("the date of Sept. 10, 1992, unless otherwise noted").get should be(
      (date(1992, 9, 10), "Sept. 10, 1992", 26)
    )
  }

  it should "return none when not a date \"33002/01/58223\"" in {
    parser.find("33002/01/58223") should be(None)
  }

  val dateText = """
    Date of end of validity: 17 JUNE 2015; A valid date
    the quick red fox 
    Date of document: 01/02/2001;
    jumped over the lazy red dog.
    Date of effect: 01/01/2002; Entry into force Date notif.
    Date of notification: 01/01/2003
    Date of end of validity: 32/12/1983; Not a valid date!
    Date of real end of validity: 31/12/1983; is a date"""

  "An EnglishParser.dateResults" should "return a seq of DateResults " in {
    val dateResultSeq = euroParser.dateResults(dateText)
    dateResultSeq.head.localDate should be(date(2015, 6, 17))
    dateResultSeq.head.dateRawText should be("17 JUNE 2015")
    dateResultSeq.head.inContext should be("Date of end of validity: 17 JUNE 2015; A valid date")
    dateResultSeq.size should be(5)
  }

  "An EnglishParser.localDates" should "return a seq of LocalDates " in {
    euroParser.localDates(dateText).head should be(date(2015, 6, 17))
    euroParser.localDates(dateText).size should be(5)
  }

  val imperfectDates =
    """
   RESOLUTION No. 537, OF 17 JUNE 2015
   Considering the Inmetro Ordinance No. 164 of 23 March 2015 approving the Conformity Assessment Requirements
   ) of June 6 th , 2001, and article 95(VI) of 
   Resolution No. 297 of February 26 th , 2002, published in the Official
   from 1 January 2019: 100%"""

  "An EnglishParser.dateResults" should "for slightly imperfectDates return a seq of DateResults " in {
    euroParser.dateResults(imperfectDates).head.localDate should be(date(2015, 6, 17))
    euroParser.dateResults(imperfectDates).size should be(5)
  }

  val page31 = """p. 31

September 2005"""

  "An EnglishParser.dateResults" should "not include page numbers from line above " in {
    euroParser.dateResults(page31).size should be(0)
  }

  val wrong = """IEC 60068-2-31-
      Part  2-31: 
7 days according to IEC 68-2-30-1980.	Rev.1"""

  "An EnglishParser.dateResults" should "not include numbers that are not dates " in {
    parser.dateResults(wrong).size should be(0)
  }

  "An EnglishParser set to locale AU should handle \"Dated: 6 – 12 – 2018\"" should "return 2018/12/06" in {
      aussieParser.find("Dated: 6 – 12 – 2018").get should be(
        (date(2018, 12, 6), "6 – 12 – 2018", 20)
      )
    }


}
