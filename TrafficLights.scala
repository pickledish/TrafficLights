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
		def tick (): List[(PointOfInterest, Direction)]
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

	class Intersection (val ratio: Ratio) extends PointOfInterest 
	{

		val currentDirection

		var waitingCars = List(0,0,0,0)
		var toDisperse: List[(PointOfInterest, Direction)] = List.empty
		var carsInTransit: List[(PointOfInterest, Direction, Int)] = List.empty

		// TODO: This is a travesty, I should not be using null just because I'm lazy
		var neighbors: List[PointOfInterest] = List(null, null, null, null)
		var nieghborDists: List[Int] = List(0,0,0,0)

		val tickCount: Int
		val disabledCount: Int

		def tick (): Intersection =
		{
			// We're doing this with NO SIDE EFFECTS
			val returner = new Intersection(this.ratio)

			// See, this is where things go a little bad
			returner.neighbors = this.neighbors
			returner.nieghborDists = this.nieghborDists
			returner.waitingCars = this.waitingCars

			// Decrement each car in transit by one tick, then add any which arrived at destination to returner
			// Note, this also gets fucked by our quest for no-side-effect programming
			val newCarsInTransit   = carsInTransit map { c => (c._1, c._2, (c._3 - 1)) }
			returner.toDisperse    = newCarsInTransit filter { c => c._3 == 0 } map { c => (c._1, c._2) }
			returner.carsInTransit = newCarsInTransit filter { c => c._3 > 0 }

			// If the intersection is disabled, count down by one, then do nothing
			if (disabledCount > 0)
			{
				returner.waitingCars = this.waitingCars
				returner.disabledCount = this.disabledCount - 1
				return returner
			}

			returner.tickCount = this.tickCount + 1

			// Which way is traffic currently travelling through this intersection?
			val oneWay = currentDirection
			val otherWay = (currentDirection + 2) % 4

			// Let one car in each currently-green direction pass, and add them to the transit queue (stasis)
			if (waitingCars(oneWay) > 0) {
				returner.waitingCars = returner.waitingCars.updated(oneWay, returner.waitingCars(oneWay) - 1)
				returner.carsInTransit = (neighbors(otherWay), oneWay, nieghborDists(otherWay)) :: returner.carsInTransit
			}

			if (waitingCars(otherWay) > 0) {
				returner.waitingCars = returner.waitingCars.updated(otherWay, returner.waitingCars(otherWay) - 1)
				returner.carsInTransit = (neighbors(oneWay), otherWay, nieghborDists(oneWay)) :: returner.carsInTransit
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
		def tick (): List[(PointOfInterest, Direction)] = List.empty

		def addWaitingCar(where: Direction, howMany: Int = 1): Unit = 
			{ arrivedCars = arrivedCars.updated(where, arrivedCars(where) + howMany) }

	}

}



