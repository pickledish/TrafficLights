import TrafficLights._

object main 
{
	def main(args: Array[String]): Unit = 
	{
		val test1 = Ratio(4, 7, NORTH)
		val inty1 = new Intersection(test1)

		val test2 = Ratio(5, 5, WEST)
		val inty2 = new Intersection(test2)

		inty1.setNeighbor(SOUTH, inty2, 1)
		inty1.setNeighbor(WEST, new Endpoint, 10)
		inty1.setNeighbor(NORTH, new Endpoint, 10)
		inty1.setNeighbor(EAST, new Endpoint, 10)

		inty2.setNeighbor(SOUTH, new Endpoint, 10)
		inty2.setNeighbor(WEST, new Endpoint, 10)
		inty2.setNeighbor(NORTH, inty1, 1)
		inty2.setNeighbor(EAST, new Endpoint, 10)

		inty1.addWaitingCar(NORTH, 10)

		var toDisperse: List[(PointOfInterest, Direction)] = List.empty

		for ( i <- 0 to 20 ) 
		{
			println(s"Starting step ${i}")
			println(s"inty1 waiting car list is ${inty1.waitingCars}")
			println(s"inty2 waiting car list is ${inty2.waitingCars}")
			println()
			toDisperse = inty1.tick() ::: toDisperse
			toDisperse = inty2.tick() ::: toDisperse
			toDisperse foreach { c => c._1 addWaitingCar c._2 }
			toDisperse = List.empty
		}

	}
}









