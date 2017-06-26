import State._

object main 
{
	def main(args: Array[String]): Unit = 
	{
		val thisState = State.fromFile("test1.txt")
		//List.range(0,20) foreach { i => println(s"Starting step ${i}"); thisState.tickAll }

		// thisState.startSections foreach println
		// println()
		// thisState.twiddle.startSections foreach println

		thisState.startSections foreach { i => println(i.ratio) }
		thisState.startSections foreach { i => println(i.waitingCars) }
		println(thisState.getScore)
		thisState.startSections foreach { i => println(i.ratio) }
		thisState.startSections foreach { i => println(i.waitingCars) }
		println(thisState.getScore)
		thisState.startSections foreach { i => println(i.ratio) }
		thisState.startSections foreach { i => println(i.waitingCars) }
	}
}









