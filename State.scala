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
		var linkList: List[List[String]] = List.empty

		def fromFile(file: String): State =
		{
			// Read the input from a text file, and make $size new intersections 
			val lines = Source.fromFile(file).getLines.toList
			val size = lines(0).toInt
			val intersections = List.range(0, size) map { _ => new Intersection(Ratio.random) }

			// Read in the neighbor matrix, and use each row to set neighbors of each intersection
			for ( i <- 0 until size )
			{
				val decomp: List[String] = lines(i + 2).split(",").toList
				val filtered = List.range(0, size) filter { j => decomp(j).length > 1 } 
				filtered foreach { j => intersections(i) setNeighbor ( char2dir(decomp(j)(1)), intersections(j), decomp(j)(0).asDigit ) }

				linkList = decomp :: linkList
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

		def linkIntersections(intersections: List[Intersection]): Unit =
		{
			for ( i <- 0 until intersections.length )
			{
				val decomp = linkList(i)
				val filtered = List.range(0, intersections.length) filter { j => decomp(j).length > 1 } 
				filtered foreach { j => intersections(i) setNeighbor ( char2dir(decomp(j)(1)), intersections(j), decomp(j)(0).asDigit ) }
				intersections foreach { i => Directions foreach { d => if (i.neighbors(d) == null) i setNeighbor (d, new Endpoint, 1) } }
			}
		}
	}

	class State(val startSections: List[Intersection])
	{
		val size: Int = startSections.length
		val cost: Int = getScore()

		def tickAll(currentSections: List[Intersection]): List[Intersection] = 
		{
			val newSections: List[Intersection] = currentSections map { i => i.tick }
			State.linkIntersections(newSections)
			// newSections foreach { i => println(s" The toDisperse for this new intersection is ${i.toDisperse}") }
			// newSections foreach { i => println(s" The neighbors for this new intersection is ${i.neighbors}") }
			// newSections foreach { i => println(s" The waitingCars for this new intersection is ${i.waitingCars}") }
			newSections foreach { i => i.toDisperse foreach { d => i.neighbors(d) addWaitingCar ((d + 2) % 4) } }
			return newSections
		}

		def getScore(): Int =
		{
			var s = 0
			var currentSections = startSections

			while ((currentSections flatMap (_.waitingCars)).sum != 0)
			{ 
				currentSections = tickAll(currentSections)
				s += 1 
			}

			return s
		}

		def twiddle(): State =
		{
			val index: Int = RandInt(0, size)
			val twiddled: Intersection = new Intersection(Ratio.random)

			twiddled.nieghborDists = startSections(index).nieghborDists
			Directions foreach { d => twiddled addWaitingCar (d, startSections(index).waitingCars(d)) }

			val returner: List[Intersection] = startSections.take(index) ::: twiddled :: startSections.drop(index + 1)
			State.linkIntersections(returner)
			return new State(returner)
		}
	}
}





