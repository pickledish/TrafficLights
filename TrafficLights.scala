package object TrafficLights 
{
	final val TRANSITION_TICKS = 8

	final val NORTH = 0
	final val EAST  = 1
	final val SOUTH = 2
	final val WEST  = 3
}

type Direction = Int

case class Ratio (cars_northsouth: Int, cars_eastwest: Int, startDirection: Direction) 

class Intersection (val ratio: Ratio, val neighbors: List[Intersection], val nieghborDists: List[Int])
{

	var currentDirection: Direction = ratio.startDirection
	private var waitingCars: List[Int] = [0,0,0,0]

	def tick (): Unit
	{


	}

}

At each tick, we need:
	Intersection to let exactly one car pass in its current direction, then
	if the # of cars passed so far in this direction equals the ratio number, disable for SWITCH_LENGTH (5?) ticks, then currentDirection.toggle 