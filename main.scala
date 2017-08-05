import State._
import scala.util.Random

object main 
{
	def main(args: Array[String]): Unit = 
	{
		var thisState = State.fromFile("test1.txt")
		//List.range(0,20) foreach { i => println(s"Starting step ${i}"); thisState.tickAll }

		// thisState.startSections foreach println
		// println()
		// thisState.twiddle.startSections foreach println

		// thisState.startSections foreach { i => println(i.ratio) }
		// thisState.startSections foreach { i => println(i.waitingCars) }
		// println(thisState.getScore)
		// thisState.startSections foreach { i => println(i.ratio) }
		// thisState.startSections foreach { i => println(i.waitingCars) }
		// println(thisState.getScore)
		// thisState.startSections foreach { i => println(i.ratio) }
		// thisState.startSections foreach { i => println(i.waitingCars) }

		val numIterations = 10000
		
		// Carefully selected -- Puja's orginal idea for coming up with this value was terrible

		(0 to numIterations) foreach { i =>
			val newState: State = thisState.twiddle
			thisState = newState.cost match 
			{
				case c if (c < 0) => thisState
				case c if (c < thisState.cost) => newState
				case _ => thisState
			}
		}
		println(thisState.cost)
		thisState.startSections foreach println
	}
}









