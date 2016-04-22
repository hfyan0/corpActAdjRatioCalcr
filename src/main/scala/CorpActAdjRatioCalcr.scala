import org.nirvana._
import java.io._
import org.joda.time.{Period, DateTime, LocalDate, Duration, Days}
import org.joda.time.format.DateTimeFormat

object CorpActAdjRatioCalcr {
  val dfmt = DateTimeFormat.forPattern("yyyy-MM-dd")

  def main(args: Array[String]) = {
    println("CorpActAdjRatioCalcr starts")

    if (args.length == 0) {
      println("USAGE   java -jar ... [blmg adj data file] [blmg unadj data file] [output file]")
      System.exit(1)
    }

    val blmgAdjDataFile = args(0)
    val blmgUnadjDataFile = args(1)
    val outputFile = args(2)

    val pw = new PrintWriter(new File(outputFile))

    val mapSymLsAdjOHLCB = scala.io.Source.fromFile(blmgAdjDataFile).getLines.toList.map(DataFmtAdaptors.parseBlmgFmt1(_, true)).filter(_ != None).map(_.get).groupBy(_.symbol)
    val mapSymLsUnadjOHLCB = scala.io.Source.fromFile(blmgUnadjDataFile).getLines.toList.map(DataFmtAdaptors.parseBlmgFmt1(_, true)).filter(_ != None).map(_.get).groupBy(_.symbol)

    val setAllKeys = mapSymLsAdjOHLCB.keySet.union(mapSymLsUnadjOHLCB.keySet)

    setAllKeys.foreach(k => {

      if (mapSymLsAdjOHLCB.contains(k) && mapSymLsUnadjOHLCB.contains(k)) {
        val lsAdjDatePrice = mapSymLsAdjOHLCB(k).map(x => (x.dt, x.priceBar.c)).sortBy(_._1.getMillis)
        val lsUnadjDatePrice = mapSymLsUnadjOHLCB(k).map(x => (x.dt, x.priceBar.c)).sortBy(_._1.getMillis)

        val setOfCommonDates = lsAdjDatePrice.map(_._1).toSet.intersect(lsUnadjDatePrice.map(_._1).toSet)

        val lsAdjDatePriceAligned = lsAdjDatePrice.filter(x => setOfCommonDates.contains(x._1))
        val lsUnadjDatePriceAligned = lsUnadjDatePrice.filter(x => setOfCommonDates.contains(x._1))

        val lsDateAdjUnadjRatio = lsAdjDatePriceAligned.zip(lsUnadjDatePriceAligned).map(x => (x._1._1, x._1._2 / x._2._2)).sortBy(_._1.getMillis)

        lsDateAdjUnadjRatio.foreach(tup => {

          pw.write(dfmt.print(tup._1))
          pw.write(",")
          pw.write(k)
          pw.write(",")
          pw.write(tup._2.toString)
          pw.write("\n")
        })

      }

    })

    pw.close
    println("CorpActAdjRatioCalcr ended")
  }
}

