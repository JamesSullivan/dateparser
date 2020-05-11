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
import java.time.Month
import java.time.Year
import java.util.Locale

import scala.annotation.tailrec

import fastparse.{ & => & }
import fastparse.AnyChar
import fastparse.CharIn

import fastparse.P
import fastparse.Start
// import fastparse.parserApi
import fastparse.LiteralStr
import fastparse.Parsed
// import fastparse.Parser
import fastparse._
import NoWhitespace._

/**
  * An English language explicit date parser
  *
  * @constructor creates a parser to find dates in text
  * @param locale The country part of the Locale is used for resolving ambiguous dates. Defaults
  *               to U.S. if not specified. Language part of locale is ignored as only English supported.
  */
class EnglishParser(val locale: Locale = new Locale("en", "US")) {

  private object FP {
    def AbsoluteDate[_: P]: P[(Year, Month, Int)] = locale match {
      case l: Locale if (Set("US", "BZ", "FM").contains(l.getCountry)) =>
        P(MiddleEndianFormal | LittleEndianFormal | MiddleEndian | LittleEndian | BigEndian).map {
          case (year, month, day) => (year, month, day)
        }
      case l: Locale if (Set("CN", "JP", "KR", "KP", "HU", "IR").contains(l.getCountry)) =>
        P(LittleEndianFormal | MiddleEndianFormal | BigEndian | LittleEndian | MiddleEndian)
      case _ =>
        P(LittleEndianFormal | MiddleEndianFormal | LittleEndian | BigEndian | MiddleEndian)
    }

    def Cardinal[_: P] = P(
      First | Second | Third | Fourth | Fifth | Sixth | Seventh | Eighth | Ninth | Tenth |
      Eleventh | Twelfth | Thirteenth | Fourteenth | Fifteenth | Sixteenth | Seventeenth | Eighteenth |
      Nineteenth | Twentieth | TwentyFirst | TwentySecond | TwentyThird | TwentyFourth | TwentyFifth |
      TwentySixth | TwentySeventh | TwentyEighth | TwentyNinth | Thirtieth | ThirtyFirst
    )
    def First[_: P]        = P("first" | "1st" | "1 st").map(x => 1)
    def Second[_: P]       = P("second" | "2nd" | "2 nd").map(x => 2)
    def Third[_: P]        = P("third" | "3rd" | "3 rd").map(x => 3)
    def Fourth[_: P]       = P("fourth" | "4th" | "4 th").map(x => 4)
    def Fifth[_: P]        = P("fifth" | "5th" | "5 th").map(x => 5)
    def Sixth[_: P]        = P("sixth" | "6th" | "6 th").map(x => 6)
    def Seventh[_: P]      = P("seventh" | "7th" | "7 th").map(x => 7)
    def Eighth[_: P]       = P("eighth" | "8th" | "8 th").map(x => 8)
    def Ninth[_: P]        = P("ninth" | "9th" | "9 th").map(x => 9)
    def Tenth[_: P]        = P("tenth" | "10th" | "10 th").map(x => 10)
    def Eleventh[_: P]     = P("eleventh" | "11th" | "11 th").map(x => 11)
    def Twelfth[_: P]      = P("tweflth" | "12th" | "12 th").map(x => 12)
    def Thirteenth[_: P]   = P("thirteenth" | "13th" | "13 th").map(x => 13)
    def Fourteenth[_: P]   = P("fourteenth" | "14th" | "14 th").map(x => 14)
    def Fifteenth[_: P]    = P("fifteenth" | "15th" | "15 th").map(x => 15)
    def Sixteenth[_: P]    = P("sixteenth" | "16th" | "16 th").map(x => 16)
    def Seventeenth[_: P]  = P("seventeenth" | "17th" | "17 th").map(x => 17)
    def Eighteenth[_: P]   = P("eigthteenth" | "18th" | "18 th").map(x => 18)
    def Nineteenth[_: P]   = P("nineteenth" | "19th" | "19 th").map(x => 19)
    def Twentieth[_: P]    = P("twentieth" | "20th" | "20 th").map(x => 20)
    def TwentyFirst[_: P]  = P("twenty" ~ (" " | "-") ~ "first" | "21st" | "21 st").map(x => 21)
    def TwentySecond[_: P] = P("twenty" ~ (" " | "-") ~ "second" | "22nd" | "22 nd").map(x => 22)
    def TwentyThird[_: P] =
      P("twenty" ~ (" " | "-") ~ "third" | "23rd" | "23 rd" | "20 th").map(x => 23)
    def TwentyFourth[_: P]  = P("twenty" ~ (" " | "-") ~ "fourth" | "24th" | "24 th").map(x => 24)
    def TwentyFifth[_: P]   = P("twenty" ~ (" " | "-") ~ "fifth" | "25th" | "25 th").map(x => 25)
    def TwentySixth[_: P]   = P("twenty" ~ (" " | "-") ~ "sixth" | "26th" | "26 th").map(x => 26)
    def TwentySeventh[_: P] = P("twenty" ~ (" " | "-") ~ "seventh" | "27th" | "27 th").map(x => 27)
    def TwentyEighth[_: P]  = P("twenty" ~ (" " | "-") ~ "eighth" | "28th" | "28 th").map(x => 28)
    def TwentyNinth[_: P]   = P("twenty" ~ (" " | "-") ~ "ninth" | "29th" | "29 th").map(x => 29)
    def Thirtieth[_: P]     = P("thirtieth" | "30th" | "30 th").map(x => 30)
    def ThirtyFirst[_: P]   = P("thirty" ~ (" " | "-") ~ "first" | "31st" | "31 st").map(x => 31)

    def SpecificMonth[_: P] = P(
      January | February | March | April | May | June | July | August | September |
      October | November | December
    )
    def January[_: P]  = P("january" | ("jan" ~ ".".?)).map(x => Month.JANUARY)
    def February[_: P] = P("february" | "febuary" | ("feb" ~ ".".?)).map(x => Month.FEBRUARY)
    def March[_: P]    = P("march" | ("mar" ~ ".".?)).map(x => Month.MARCH)
    def April[_: P]    = P("april" | ("apr" ~ ".".?)).map(x => Month.APRIL)
    def May[_: P]      = P("may").map(x => Month.MAY)
    def June[_: P]     = P("june" | ("jun" ~ ".".?)).map(x => Month.JUNE)
    def July[_: P]     = P("july" | ("jul" ~ ".".?)).map(x => Month.JULY)
    def August[_: P]   = P("august" | ("aug" ~ ".".?)).map(x => Month.AUGUST)
    def September[_: P] =
      P("september" | ("sept" ~ ".".?) | ("sep" ~ ".".?)).map(x => Month.SEPTEMBER)
    def October[_: P]  = P("october" | ("oct" ~ ".".?)).map(x => Month.OCTOBER)
    def November[_: P] = P("november" | ("nov" ~ ".".?)).map(x => Month.NOVEMBER)
    def December[_: P] = P("december" | ("dec" ~ ".".?)).map(x => Month.DECEMBER)
    def Months[_: P]   = P(MonthDigits | SpecificMonth)

    def Digit[_: P]        = P(CharIn("0-9"))
    def NonZeroDigit[_: P] = P(CharIn("1-9"))
    def Space[_: P]        = P(" " ~ " ".?)
    def Divider[_: P]      = P("/" | "-" | " – ")
    def DigitDiv[_: P]     = P("/" | "-" | Digit)

    def YYYYMMDD[_: P]: P[(Year, Month, Int)] = P(StrictYear ~ StrictMonth ~ StrictDay)
    def BigEndian[_: P]: P[(Year, Month, Int)] = P(
      ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ (YYYYMMDD | YearDigits ~ Divider ~ Months ~ Divider ~
      DayDigits) ~ !DigitDiv
    )
    def LittleEndian[_: P]: P[(Year, Month, Int)] =
      P(
        ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ DayDigits ~ Divider ~ Months ~ Divider ~ YearDigits ~ !DigitDiv
      ).map { case (day, month, year) => (year, month, day) }
    def MiddleEndian[_: P]: P[(Year, Month, Int)] =
      P(
        ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ Months ~ Divider ~ DayDigits ~ Divider ~ YearDigits ~ !DigitDiv
      ).map { case (month, day, year) => (year, month, day) }
    def YearDigits[_: P] = P(("19" | "20").? ~ Digit ~ Digit).!.map(yearTo4Digits(_))
    def MonthDigits[_: P] =
      P(("1" ~ ("0" | "1" | "2")) | ("0".? ~ NonZeroDigit)).!.map(x => Month.of(Integer.parseInt(x))
      )
    def DayDigits[_: P] =
      P(("3" ~ ("0" | "1")) | (("1" | "2") ~ Digit) | ("0".? ~ NonZeroDigit)).!.map(x =>
        Integer.parseInt(x)
      )
    def StrictYear[_: P] = P(("19" | "20") ~ Digit ~ Digit).!.map(x => yearTo4Digits(x))
    def StrictMonth[_: P] =
      P(("1" ~ ("0" | "1" | "2")) | ("0" ~ NonZeroDigit)).!.map(x => Month.of(Integer.parseInt(x)))
    def StrictDay[_: P] =
      P(("3" ~ ("0" | "1")) | (("1" | "2") ~ Digit) | ("0" ~ NonZeroDigit)).!.map(x =>
        Integer.parseInt(x)
      )

    def LittleEndianFormal[_: P]: P[(Year, Month, Int)] =
      P(
        DayDigits ~ ".".? ~ Space ~ SpecificMonth ~ Space ~ YearDigits |
        Cardinal ~ Space ~ "of ".? ~ SpecificMonth ~ Space ~ YearDigits
      ).map { case (day, month, year) => (year, month, day) }

    def MiddleEndianFormal[_: P]: P[(Year, Month, Int)] =
      P(
        SpecificMonth ~ Space ~ DayDigits ~ Space.? ~ ".".? ~ ",".? ~ Space ~ YearDigits |
        SpecificMonth ~ Space ~ Cardinal ~ Space.? ~ ",".? ~ Space ~ YearDigits
      ).map { case (month, day, year) => (year, month, day) }

    def consumeString[_: P]: P[String] = P((!AbsoluteDate ~ AnyChar).rep ~ AbsoluteDate.!)
    def consumeTuple[_: P]: P[Seq[(Year, Month, Int)]] = P(
      ((!AbsoluteDate ~ AnyChar).rep ~ AbsoluteDate).rep
    )

    private def yearTo4Digits(s: String): Year =
      Year.of(Integer.parseInt(s) match {
        case year: Int if (year < 50)               => 2000 + year
        case year: Int if (year > 50 && year < 100) => 1900 + year
        case year: Int                              => year
      })

  }

  /**
    * @param s the string to be parsed
    * @param from optional index of where to start parsing the string from
    * @return an option tuple with the LocalDate, the raw string from the matched date
    *           and the index after the last character found in the date (not the first).
    */
  def find(s: String, from: Int = 0): Option[(LocalDate, String, Int)] =
    parse(s.toLowerCase, FP.consumeString(_), startIndex = from) match {
      case Parsed.Failure(p, f, i) =>
        None
      case Parsed.Success(value, successIndex) =>
        val Parsed.Success((year, month, day), index) =
          parse(value, FP.AbsoluteDate(_), startIndex = 0);
        try {
          Some(
            (
              LocalDate.of(year.getValue, month.getValue, day),
              s.substring(successIndex - value.length, successIndex).trim,
              successIndex
            )
          )
        } catch {
          case dateException: java.time.DateTimeException => find(s, successIndex)
          // let anything else blow up
        }
    }

  /**
    * @param s the string to be parsed
    * @return a seq of DateResults found.
    */
  def dateResults(s: String): Seq[DateResult] = {
    @tailrec
    def getResults(from: Int, accum: Seq[DateResult]): Seq[DateResult] = find(s, from) match {
      case None =>
        accum
      case Some((localDate, raw, endIndex)) =>
        getResults(
          (endIndex),
          accum :+ DateResult(
            localDate,
            raw,
            endIndex - raw.length,
            getContext(endIndex, raw.length),
            locale.getCountry
          )
        )
    }

    def getContext(index: Int, len: Int): String = {
      var startPos   = index - len
      val lowerBound = Math.max((startPos - len) - 80, 0)
      while (startPos > lowerBound && !"\r\n.".contains(s.charAt(startPos))) {
        startPos = startPos - 1
      }
      if ("\r\n.".contains(s.charAt(startPos))) startPos = startPos + 1
      var endPos     = index
      val upperBound = Math.min((endPos + 80), s.length - 1)
      while (endPos < upperBound && !"\r\n.".contains(s.charAt(endPos))) { endPos = endPos + 1 }
      s.substring(startPos, endPos).trim
    }
    getResults(0, Seq[DateResult]())
  }

  /**
    * @param s the string to be parsed
    * @return a seq of LocalDates found in the string
    */
  def localDates(s: String): Seq[LocalDate] = parse(s.toLowerCase, FP.consumeTuple(_)) match {
    case Parsed.Failure(p, f, i) =>
      Nil
    case Parsed.Success(value, successIndex) =>
      try {
        value.map { case (y: Year, m: Month, d: Int) => LocalDate.of(y.getValue, m.getValue, d) }
      } catch {
        case dateException: java.time.DateTimeException => Nil
        // let any other errors bubble up
      }
  }

}

/**
  * A parsed LocalDate enhanced with additional useful information
  *
  * @constructor create a new DateResult
  * @param date as the Java 8 java.time.LocalDate
  * @param dateRawText the date as text in the string that was parsed
  * @param index the position in the string the date was discovered
  * @param inContext relevant surrounding text within 80 characters if same/line sentence.
  * @param normalizedType country used to determine precedence rules in case of ambiguity
  */
case class DateResult(
    localDate: LocalDate,
    dateRawText: String,
    index: Int,
    inContext: String,
    normalizedType: String
) extends Ordered[DateResult] {
  def compare(y: DateResult): Int = this.localDate.compareTo(y.localDate)
}
