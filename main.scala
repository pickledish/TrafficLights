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

		val numIterations = 10000
		
		// Carefully selected -- Puja's orginal idea for coming up with this value was terrible
		val alpha: Double = 0.035
		val stateListBuf: ListBuffer[State] = ListBuffer.empty[State]

		(0 to numIterations) foreach { i =>

			val newState: State = Random.nextDouble match 
			{
				case p if (p > 0.98) => currentState.shuffleEnabledEdges
				case p if (p > 0.49) => currentState.oneLarger
				case _ => currentState.oneSmaller
			}

			val r: Double = exp ((-1) * alpha * (newState.cost - currentState.cost))
			val z: Double = Random.nextDouble

			currentState = newState.cost match 
			{
				case c if (c < 0) => currentState
				case c if (r > z) => newState
				case _ => currentState
			}

			// println(s"The state after iteration ${i} has ${currentState.edgeCount} edges and its cost is ${currentState.cost}")
			stateListBuf += currentState
		}
	}
}









