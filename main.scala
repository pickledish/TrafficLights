import State._

object main 
{
	def main(args: Array[String]): Unit = 
	{
		val thisState = State.fromFile("test1.txt")
		List.range(0,20) foreach { i => println(s"Starting step ${i}"); thisState.tick }
	}
}









