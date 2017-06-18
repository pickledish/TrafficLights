package object TrafficLights 
{
	type Direction = Int

	final val TRANSITION_TICKS = 8

	final val NORTH: Direction = 0
	final val EAST:	 Direction = 1
	final val SOUTH: Direction = 2
	final val WEST:	 Direction = 3
}

package TrafficLights
{
	case class Ratio (cars_northsouth: Int, cars_eastwest: Int, startDirection: Direction) 

	class Intersection (val ratio: Ratio, val neighbors: List[Intersection], val nieghborDists: List[Int])
	{

		private var currentDirection = ratio.startDirection
		private var waitingCars = List(0,0,0,0)
		private var stasisCars: List[(Intersection, Direction, Int)] = List.empty

		private var tickCount: Int = 0
		private var disabledCount: Int = 0

		def tick (): List[(Intersection, Direction)] =
		{
			// If the intersection is disabled, count down by one, then do nothing
			if (disabledCount > 0) { disabledCount -= 1; return List.empty }

			tickCount += 1

			// Decrement each car in transit by one tick, then add any which arrived at destination to returner
			stasisCars = stasisCars map { c => (c._1, c._2, (c._3 - 1)) }
			val returner = stasisCars filter { c => c._3 == 0 } map { c => (c._1, c._2) }
			stasisCars = stasisCars filter { c => c._3 > 0 }

			// Which way is traffic currently travelling through this intersection?
			val oneWay = currentDirection
			val otherWay = (currentDirection + 2) % 4

			// Let one car in each currently-green direction pass, and add them to the transit queue (stasis)
			if (waitingCars(oneWay) > 0) {
				waitingCars = waitingCars.updated(oneWay, waitingCars(oneWay) - 1)
				stasisCars = (neighbors(otherWay), oneWay, nieghborDists(otherWay)) :: stasisCars
			}

			if (waitingCars(otherWay) > 0) {
				waitingCars = waitingCars.updated(otherWay, waitingCars(otherWay) - 1)
				stasisCars = (neighbors(oneWay), otherWay, nieghborDists(oneWay)) :: stasisCars
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
				currentDirection = (currentDirection + 1) % 4
				disabledCount = TRANSITION_TICKS
			}

			return returner
		}

	}

}