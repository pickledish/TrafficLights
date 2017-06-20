import TrafficLights._
import scala.io.Source
import scala.language.implicitConversions

object main 
{

	def main(args: Array[String]): Unit = 
	{
		// Read the input from a text file, and make $size new intersections 
		val lines = Source.fromFile("test1.txt").getLines.toList
		val size = lines(0).toInt
		val intersections = List.range(0, size) map { _ => new Intersection(Ratio(1,1,NORTH)) }

		// Read in the neighbor matrix, and use each row to set neighbors of each intersection
		for ( i <- 0 until size )
		{
			val decomp: List[List[Char]] = lines(i+2).split(",").toList map { _.toList }
			val filtered = List.range(0, size) filter { j => decomp(j).length == 2 } 
			filtered foreach { j => intersections(i) setNeighbor ( char2dir(decomp(j)(1)), intersections(j), decomp(j)(0).asDigit ) }
		}

		// For each intersection, set any null neighbors to a new endpoint
		intersections foreach { i => Directions foreach { d => if (i.neighbors(d) == null) i setNeighbor (d, new Endpoint, 10) } }

		intersections(0).addWaitingCar(NORTH, 10)
		intersections(1).addWaitingCar(WEST, 10)

		for ( i <- 0 to 20 ) 
		{
			println(s"Starting step ${i}")
			intersections foreach println

			val toDisperse: List[(PointOfInterest, Direction)] = (intersections map {i => i.tick}).flatten
			toDisperse foreach { c => c._1 addWaitingCar c._2 }
		}

	}
}









