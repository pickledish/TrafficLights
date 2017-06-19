import TrafficLights._

object main 
{
	def main(args: Array[String]): Unit = 
	{
		val inty1 = new Intersection(Ratio(4, 7, NORTH))
		val inty2 = new Intersection(Ratio(5, 5, WEST))
		val inty3 = new Intersection(Ratio(7, 2, WEST))

		inty1.setNeighbor(SOUTH, inty2, 1)
		inty1.setNeighbor(WEST, new Endpoint, 10)
		inty1.setNeighbor(NORTH, new Endpoint, 10)
		inty1.setNeighbor(EAST, new Endpoint, 10)

		inty2.setNeighbor(SOUTH, new Endpoint, 10)
		inty2.setNeighbor(WEST, new Endpoint, 10)
		inty2.setNeighbor(NORTH, inty1, 1)
		inty2.setNeighbor(EAST, new Endpoint, 10)

		inty3.setNeighbor(SOUTH, new Endpoint, 10)
		inty3.setNeighbor(WEST, new Endpoint, 10)
		inty3.setNeighbor(NORTH, new Endpoint, 10)
		inty3.setNeighbor(EAST, inty2, 1)

		inty1.addWaitingCar(NORTH, 10)
		inty3.addWaitingCar(WEST, 10)

		var toDisperse: List[(PointOfInterest, Direction)] = List.empty

		for ( i <- 0 to 20 ) 
		{
			println(s"Starting step ${i}")
			println(inty1)
			println(inty2)
			println(inty3)
			println()

			toDisperse = inty1.tick() ::: toDisperse
			toDisperse = inty2.tick() ::: toDisperse
			toDisperse = inty3.tick() ::: toDisperse

			toDisperse foreach { c => c._1 addWaitingCar c._2 }
			toDisperse = List.empty
		}

	}
}









