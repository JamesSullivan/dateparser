/*
 * Copyright (c) 2017 James Sullivan
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

import fastparse.all.{ & => & }
import fastparse.all.AnyChar
import fastparse.all.CharIn

import fastparse.all.P
import fastparse.all.Start
import fastparse.all.parserApi
import fastparse.all.LiteralStr
import fastparse.core.Parsed
import fastparse.core.Parser

/**
  * An English language explicit date parser
  *
  * @constructor creates a parser to find dates in text
  * @param locale The country part of the Locale is used for resolving ambiguous dates. Defaults
  *               to U.S. if not specified. Language part of locale is ignored as only English supported.
  */
class EnglishParser(val locale: Locale = new Locale("en", "US")) {

  private object FP {
    val AbsoluteDate: P[(Year, Month, Int)] = locale match {
      case l: Locale if (Set("US", "BZ", "FM").contains(l.getCountry)) =>
        P(MiddleEndianFormal | LittleEndianFormal | MiddleEndian | LittleEndian | BigEndian).map {
          case (year, month, day) => (year, month, day)
        }
      case l: Locale if (Set("CN", "JP", "KR", "KP", "HU", "IR").contains(l.getCountry)) =>
        P(LittleEndianFormal | MiddleEndianFormal | BigEndian | LittleEndian | MiddleEndian)
      case _ =>
        P(LittleEndianFormal | MiddleEndianFormal | LittleEndian | BigEndian | MiddleEndian)
    }

    val Cardinal = P(
      First | Second | Third | Fourth | Fifth | Sixth | Seventh | Eighth | Ninth | Tenth |
      Eleventh | Twelfth | Thirteenth | Fourteenth | Fifteenth | Sixteenth | Seventeenth | Eighteenth |
      Nineteenth | Twentieth | TwentyFirst | TwentySecond | TwentyThird | TwentyFourth | TwentyFifth |
      TwentySixth | TwentySeventh | TwentyEighth | TwentyNinth | Thirtieth | ThirtyFirst
    )
    val First         = P("first" | "1st" | "1 st").map(x => 1)
    val Second        = P("second" | "2nd" | "2 nd").map(x => 2)
    val Third         = P("third" | "3rd" | "3 rd").map(x => 3)
    val Fourth        = P("fourth" | "4th" | "4 th").map(x => 4)
    val Fifth         = P("fifth" | "5th" | "5 th").map(x => 5)
    val Sixth         = P("sixth" | "6th" | "6 th").map(x => 6)
    val Seventh       = P("seventh" | "7th" | "7 th").map(x => 7)
    val Eighth        = P("eighth" | "8th" | "8 th").map(x => 8)
    val Ninth         = P("ninth" | "9th" | "9 th").map(x => 9)
    val Tenth         = P("tenth" | "10th" | "10 th").map(x => 10)
    val Eleventh      = P("eleventh" | "11th" | "11 th").map(x => 11)
    val Twelfth       = P("tweflth" | "12th" | "12 th").map(x => 12)
    val Thirteenth    = P("thirteenth" | "13th" | "13 th").map(x => 13)
    val Fourteenth    = P("fourteenth" | "14th" | "14 th").map(x => 14)
    val Fifteenth     = P("fifteenth" | "15th" | "15 th").map(x => 15)
    val Sixteenth     = P("sixteenth" | "16th" | "16 th").map(x => 16)
    val Seventeenth   = P("seventeenth" | "17th" | "17 th").map(x => 17)
    val Eighteenth    = P("eigthteenth" | "18th" | "18 th").map(x => 18)
    val Nineteenth    = P("nineteenth" | "19th" | "19 th").map(x => 19)
    val Twentieth     = P("twentieth" | "20th" | "20 th").map(x => 20)
    val TwentyFirst   = P("twenty" ~ (" " | "-") ~ "first" | "21st" | "21 st").map(x => 21)
    val TwentySecond  = P("twenty" ~ (" " | "-") ~ "second" | "22nd" | "22 nd").map(x => 22)
    val TwentyThird   = P("twenty" ~ (" " | "-") ~ "third" | "23rd" | "23 rd" | "20 th").map(x => 23)
    val TwentyFourth  = P("twenty" ~ (" " | "-") ~ "fourth" | "24th" | "24 th").map(x => 24)
    val TwentyFifth   = P("twenty" ~ (" " | "-") ~ "fifth" | "25th" | "25 th").map(x => 25)
    val TwentySixth   = P("twenty" ~ (" " | "-") ~ "sixth" | "26th" | "26 th").map(x => 26)
    val TwentySeventh = P("twenty" ~ (" " | "-") ~ "seventh" | "27th" | "27 th").map(x => 27)
    val TwentyEighth  = P("twenty" ~ (" " | "-") ~ "eighth" | "28th" | "28 th").map(x => 28)
    val TwentyNinth   = P("twenty" ~ (" " | "-") ~ "ninth" | "29th" | "29 th").map(x => 29)
    val Thirtieth     = P("thirtieth" | "30th" | "30 th").map(x => 30)
    val ThirtyFirst   = P("thirty" ~ (" " | "-") ~ "first" | "31st" | "31 st").map(x => 31)

    val SpecificMonth = P(
      January | Febuary | March | April | May | June | July | August | September |
      October | November | December
    )
    val January   = P("january" | ("jan" ~ ".".?)).map(x => Month.JANUARY)
    val Febuary   = P("february" | ("feb" ~ ".".?)).map(x => Month.FEBRUARY)
    val March     = P("march" | ("mar" ~ ".".?)).map(x => Month.MARCH)
    val April     = P("april" | ("apr" ~ ".".?)).map(x => Month.APRIL)
    val May       = P("may").map(x => Month.MAY)
    val June      = P("june" | ("jun" ~ ".".?)).map(x => Month.JUNE)
    val July      = P("july" | ("jul" ~ ".".?)).map(x => Month.JULY)
    val August    = P("august" | ("aug" ~ ".".?)).map(x => Month.AUGUST)
    val September = P("september" | ("sept" ~ ".".?) | ("sep" ~ ".".?)).map(x => Month.SEPTEMBER)
    val October   = P("october" | ("oct" ~ ".".?)).map(x => Month.OCTOBER)
    val November  = P("november" | ("nov" ~ ".".?)).map(x => Month.NOVEMBER)
    val December  = P("december" | ("dec" ~ ".".?)).map(x => Month.DECEMBER)
    val Months    = P(MonthDigits | SpecificMonth)

    val Digit        = P(CharIn('0' to '9'))
    val NonZeroDigit = P(CharIn('1' to '9'))
    val Space        = P(" " ~ " ".?)
    val Divider      = ("/" | "-")
    val DigitDiv     = ("/" | "-" | Digit)

    val YYYYMMDD: P[(Year, Month, Int)] = P(StrictYear ~ StrictMonth ~ StrictDay)
    val BigEndian: P[(Year, Month, Int)] = P(
      ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ (YYYYMMDD | YearDigits ~ Divider ~ Months ~ Divider ~
      DayDigits) ~ !DigitDiv
    )
    val LittleEndian: P[(Year, Month, Int)] = P(
      ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ DayDigits ~ Divider ~ Months ~ Divider ~ YearDigits ~ !DigitDiv
    ).map { case (day, month, year) => (year, month, day) }
    val MiddleEndian: P[(Year, Month, Int)] = P(
      ((!DigitDiv ~ AnyChar) | (Start ~ &(Digit))) ~ Months ~ Divider ~ DayDigits ~ Divider ~ YearDigits ~ !DigitDiv
    ).map { case (month, day, year) => (year, month, day) }
    val YearDigits = P(("19" | "20").? ~ Digit ~ Digit).!.map(yearTo4Digits(_))
    val MonthDigits = P(("1" ~ ("0" | "1" | "2")) | ("0".? ~ NonZeroDigit)).!.map(
      x => Month.of(Integer.parseInt(x))
    )
    val DayDigits = P(("3" ~ ("0" | "1")) | (("1" | "2") ~ Digit) | ("0".? ~ NonZeroDigit)).!.map(
      x => Integer.parseInt(x)
    )
    val StrictYear = P(("19" | "20") ~ Digit ~ Digit).!.map(x => yearTo4Digits(x))
    val StrictMonth =
      P(("1" ~ ("0" | "1" | "2")) | ("0" ~ NonZeroDigit)).!.map(x => Month.of(Integer.parseInt(x)))
    val StrictDay = P(("3" ~ ("0" | "1")) | (("1" | "2") ~ Digit) | ("0" ~ NonZeroDigit)).!.map(
      x => Integer.parseInt(x)
    )

    val LittleEndianFormal: P[(Year, Month, Int)] = P(
      DayDigits ~ ".".? ~ Space ~ SpecificMonth ~ Space ~ YearDigits |
      Cardinal ~ Space ~ "of ".? ~ SpecificMonth ~ Space ~ YearDigits
    ).map { case (day, month, year) => (year, month, day) }

    val MiddleEndianFormal: P[(Year, Month, Int)] = P(
      SpecificMonth ~ Space ~ DayDigits ~ Space.? ~ ".".? ~ ",".? ~ Space ~ YearDigits |
      SpecificMonth ~ Space ~ Cardinal ~ Space.? ~ ",".? ~ Space ~ YearDigits
    ).map { case (month, day, year) => (year, month, day) }

    val consumeString: P[String] = P((!AbsoluteDate ~ AnyChar).rep ~ AbsoluteDate.!)
    val consumeTuple: P[Seq[(Year, Month, Int)]] = P(
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
  def parse(s: String, from: Int = 0): Option[(LocalDate, String, Int)] =
    FP.consumeString.parse(s.toLowerCase, from) match {
      case Parsed.Failure(p, f, i) =>
        None
      case Parsed.Success(value, successIndex) =>
        val Parsed.Success((year, month, day), index) = FP.AbsoluteDate.parse(value, 0);
        try {
          Some(
            (LocalDate.of(year.getValue, month.getValue, day),
             s.substring(successIndex - value.length, successIndex).trim,
             successIndex)
          )
        } catch {
          case dateException: java.time.DateTimeException => parse(s, successIndex)
          // let anything else blow up
        }
    }

  /**
    * @param s the string to be parsed
    * @return a seq of DateResults found.
    */
  def dateResults(s: String): Seq[DateResult] = {
    @tailrec
    def getResults(from: Int, accum: Seq[DateResult]): Seq[DateResult] = parse(s, from) match {
      case None =>
        accum
      case Some((localDate, raw, endIndex)) =>
        getResults((endIndex),
                   accum :+ DateResult(localDate,
                                       raw,
                                       endIndex - raw.length,
                                       getContext(endIndex, raw.length),
                                       locale.getCountry))
    }

    def getContext(index: Int, len: Int): String = {
      var startPos   = index - len
      var lowerBound = Math.max((startPos - len) - 80, 0)
      while (startPos > lowerBound && !"\r\n.".contains(s.charAt(startPos))) {
        startPos = startPos - 1
      }
      if ("\r\n.".contains(s.charAt(startPos))) startPos = startPos + 1
      var endPos     = index
      var upperBound = Math.min((endPos + 80), s.length - 1)
      while (endPos < upperBound && !"\r\n.".contains(s.charAt(endPos))) { endPos = endPos + 1 }
      s.substring(startPos, endPos).trim
    }
    getResults(0, Seq[DateResult]())
  }

  /**
    * @param s the string to be parsed
    * @return a seq of LocalDates found in the string
    */
  def localDates(s: String): Seq[LocalDate] = FP.consumeTuple.parse(s.toLowerCase) match {
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
case class DateResult(localDate: LocalDate,
                      dateRawText: String,
                      index: Int,
                      inContext: String,
                      normalizedType: String)
    extends Ordered[DateResult] {
  def compare(y: DateResult): Int = this.localDate.compareTo(y.localDate)
}
