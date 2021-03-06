import scala.util.Random

package object TrafficLights 
{
	type Direction = Int

	final val TRANSITION_TICKS = 3
	final val MAX_STREAM_LENGTH = 10

	final val NORTH: Direction = 0
	final val EAST:	 Direction = 1
	final val SOUTH: Direction = 2
	final val WEST:	 Direction = 3
	final val Directions = List(NORTH, EAST, SOUTH, WEST)

	def RandInt(min: Int, max: Int): Int = Random.nextInt(max - min) + min

	trait PointOfInterest 
	{
		def tick (): PointOfInterest
		def addWaitingCar(where: Direction, howMany: Int = 1): Unit
	}
}

package TrafficLights
{
	case class Ratio (cars_northsouth: Int, cars_eastwest: Int, startDirection: Direction) 

	object Ratio 
	{
		def random(): Ratio = 
		{
			val northCars: Int = RandInt(1,MAX_STREAM_LENGTH)
			val westCars: Int  = RandInt(1,MAX_STREAM_LENGTH)
			val dir: Direction = RandInt(0,2)
			return Ratio(northCars, westCars, dir)
		}
	}

	class Intersection (var ratio: Ratio) extends PointOfInterest 
	{

		var currentDirection = ratio.startDirection

		var waitingCars = List(0,0,0,0)
		var carsInTransit: List[(Direction, Int)] = List.empty
		var toDisperse: List[Direction] = List.empty

		var neighbors: List[PointOfInterest] = List(null, null, null, null)
		var nieghborDists = List(0,0,0,0)

		var tickCount: Int = 0
		var disabledCount: Int = 0

		def tick (): Intersection =
		{
			// Trying to do this without side effects
			val returner = new Intersection(ratio)
			returner.nieghborDists = nieghborDists
			returner.currentDirection = currentDirection
			returner.waitingCars = waitingCars

			// Decrement each car in transit by one tick, then add any which arrived at destination to returner
			returner.carsInTransit = carsInTransit map    { c => (c._1, (c._2 - 1)) }
			returner.toDisperse    = returner.carsInTransit filter { c => c._2 == 0 } map { c => c._1 }
			returner.carsInTransit = returner.carsInTransit filter { c => c._2 > 0 }

			// If the intersection is disabled, count down by one, then do nothing
			if (disabledCount > 0)
			{
				returner.disabledCount = disabledCount - 1
				return returner 
			}

			returner.tickCount = tickCount + 1

			// Which way is traffic currently travelling through this intersection?
			val oneWay = currentDirection
			val otherWay = (currentDirection + 2) % 4

			// Let one car in each currently-green direction pass, and add them to the transit queue (stasis)
			if (waitingCars(oneWay) > 0) {
				returner.waitingCars = returner.waitingCars.updated(oneWay, returner.waitingCars(oneWay) - 1)
				returner.carsInTransit = (otherWay, nieghborDists(otherWay)) :: returner.carsInTransit
			}

			if (waitingCars(otherWay) > 0) {
				returner.waitingCars = returner.waitingCars.updated(otherWay, returner.waitingCars(otherWay) - 1)
				returner.carsInTransit = (oneWay, nieghborDists(oneWay)) :: returner.carsInTransit
			}

			// How many more cars are we going to let pass before the light turns red?
			val limit = currentDirection match {
				case NORTH => ratio.cars_northsouth
				case SOUTH => ratio.cars_northsouth
				case EAST  => ratio.cars_eastwest
				case WEST  => ratio.cars_eastwest
			}

			// If we've reached that limit, the light turns red, and we start the transition to other direction
			if (tickCount == limit) {
				returner.tickCount = 0
				returner.currentDirection = (currentDirection + 1) % 4
				returner.disabledCount = TRANSITION_TICKS
			}

			return returner
		}

		def setNeighbor(where: Direction, which: PointOfInterest, howFar: Int): Unit = 
			{ 
				neighbors = neighbors.updated(where, which)
				nieghborDists = nieghborDists.updated(where, howFar) 
			}

		def addWaitingCar(where: Direction, howMany: Int = 1): Unit = 
			{ waitingCars = waitingCars.updated(where, waitingCars(where) + howMany) }

		override def toString: String = 
			s"Intersection of ratio ${ratio}, and waiting cars are ${waitingCars}"

	}

	class Endpoint () extends PointOfInterest
	{
		private var arrivedCars = List(0,0,0,0)

		// Does nothing, really
		def tick (): Endpoint = this

		def addWaitingCar(where: Direction, howMany: Int = 1): Unit = 
			{ arrivedCars = arrivedCars.updated(where, arrivedCars(where) + howMany) }

	}

}



