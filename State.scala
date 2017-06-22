import TrafficLights._
import scala.io.Source

package object State
{
	def char2dir (c: Char): Direction = c match 
	{
		case 'N' => NORTH
		case 'E' => EAST
		case 'S' => SOUTH
		case 'W' => WEST
	}
}

package State
{
	object State
	{
		def fromFile(file: String): State =
		{
			// Read the input from a text file, and make $size new intersections 
			val lines = Source.fromFile(file).getLines.toList
			val size = lines(0).toInt
			val intersections = List.range(0, size) map { _ => new Intersection(Ratio.random) }

			// Read in the neighbor matrix, and use each row to set neighbors of each intersection
			for ( i <- 0 until size )
			{
				val decomp: List[List[Char]] = lines(i + 2).split(",").toList map { _.toList }
				val filtered = List.range(0, size) filter { j => decomp(j).length == 2 } 
				filtered foreach { j => intersections(i) setNeighbor ( char2dir(decomp(j)(1)), intersections(j), decomp(j)(0).asDigit ) }
			}

			for ( i <- 0 until size )
			{
				val supplies: List[String] = lines(i + size + 3).split(",").toList
				Directions foreach { d => intersections(i) addWaitingCar (d, supplies(d).toInt) }
			}

			// For each intersection, set any null neighbors to a new endpoint
			intersections foreach { i => Directions foreach { d => if (i.neighbors(d) == null) i setNeighbor (d, new Endpoint, 1) } }

			return new State(intersections)
		}
	}

	class State(val intersections: List[Intersection])
	{
		def tickAll(): Unit = 
		{
			//intersections foreach println
			val toDisperse: List[(PointOfInterest, Direction)] = (intersections map {i => i.tick}).flatten
			toDisperse foreach { c => c._1 addWaitingCar c._2 }
		}

		def getScore(): Int =
		{
			var s = 0
			while ((intersections flatMap (_.waitingCars)).sum != 0) { tickAll(); s += 1 }
			return s
		}

		def twiddle(): State =
		{
			val index: Int = RandInt(0, intersections.length)
			val twiddled: Intersection = new Intersection(Ratio.random)
			Directions foreach { d => twiddled addWaitingCar (d, intersections(index).waitingCars(d)) }
			Directions foreach { d => if (twiddled.neighbors(d) == null) twiddled setNeighbor (d, new Endpoint, 1) }
			return new State(intersections.take(index) ::: twiddled :: intersections.drop(index + 1))
		}
	}
}





