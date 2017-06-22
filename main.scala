import State._

object main 
{
	def main(args: Array[String]): Unit = 
	{
		val thisState = State.fromFile("test1.txt")
		//List.range(0,20) foreach { i => println(s"Starting step ${i}"); thisState.tickAll }

		thisState.intersections foreach println
		println()
		thisState.twiddle.intersections foreach println

		// thisState.intersections foreach { i => println(i.ratio) }
		// thisState.intersections foreach { i => println(i.waitingCars) }
		// println(thisState.getScore)
		// thisState.intersections foreach { i => println(i.ratio) }
		// thisState.intersections foreach { i => println(i.waitingCars) }
	}
}









