package object TrafficLights 
{
	type Direction = Int

	trait PointOfInterest 
	{
		def tick (): List[(PointOfInterest, Direction)]
		def addWaitingCar(where: Direction, howMany: Int = 1): Unit
	}

	final val TRANSITION_TICKS = 3

	final val NORTH: Direction = 0
	final val EAST:	 Direction = 1
	final val SOUTH: Direction = 2
	final val WEST:	 Direction = 3
}

package TrafficLights
{
	case class Ratio (cars_northsouth: Int, cars_eastwest: Int, startDirection: Direction) 

	class Intersection (val ratio: Ratio) extends PointOfInterest 
	{

		var currentDirection = ratio.startDirection
		var waitingCars = List(0,0,0,0)
		var carsInTransit: List[(PointOfInterest, Direction, Int)] = List.empty

		// TODO: This is a travesty, no using null just because I'm lazy
		var neighbors: List[PointOfInterest] = List(null, null, null, null)
		var nieghborDists: List[Int] = List(0,0,0,0)

		var tickCount: Int = 0
		var disabledCount: Int = 0

		def tick (): List[(PointOfInterest, Direction)] =
		{
			// Decrement each car in transit by one tick, then add any which arrived at destination to returner
			carsInTransit = carsInTransit map { c => (c._1, c._2, (c._3 - 1)) }
			val returner = carsInTransit filter { c => c._3 == 0 } map { c => (c._1, c._2) }
			carsInTransit = carsInTransit filter { c => c._3 > 0 }

			// If the intersection is disabled, count down by one, then do nothing
			if (disabledCount > 0) { disabledCount -= 1; return returner }

			tickCount += 1

			// Which way is traffic currently travelling through this intersection?
			val oneWay = currentDirection
			val otherWay = (currentDirection + 2) % 4

			// Let one car in each currently-green direction pass, and add them to the transit queue (stasis)
			if (waitingCars(oneWay) > 0) {
				waitingCars = waitingCars.updated(oneWay, waitingCars(oneWay) - 1)
				carsInTransit = (neighbors(otherWay), oneWay, nieghborDists(otherWay)) :: carsInTransit
			}

			if (waitingCars(otherWay) > 0) {
				waitingCars = waitingCars.updated(otherWay, waitingCars(otherWay) - 1)
				carsInTransit = (neighbors(oneWay), otherWay, nieghborDists(oneWay)) :: carsInTransit
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
				tickCount = 0
				currentDirection = (currentDirection + 1) % 4
				disabledCount = TRANSITION_TICKS
			}

			return returner
		}

		def setNeighbor(where: Direction, which: PointOfInterest, howFar: Int): Unit = 
			{ neighbors = neighbors.updated(where, which); nieghborDists = nieghborDists.updated(where, howFar) }

		def addWaitingCar(where: Direction, howMany: Int = 1): Unit = 
			{ waitingCars = waitingCars.updated(where, waitingCars(where) + howMany) }

		override def toString: String = 
			s"Intersection, waiting cars are ${waitingCars} and in-transit cars (past this intersection) is ${carsInTransit.length}"

	}

	class Endpoint () extends PointOfInterest
	{

		private var arrivedCars = List(0,0,0,0)

		// Does nothing, really
		def tick (): List[(PointOfInterest, Direction)] = List.empty

		def addWaitingCar(where: Direction, howMany: Int = 1): Unit = 
			{ arrivedCars = arrivedCars.updated(where, arrivedCars(where) + howMany) }

	}

}



