## Traffic Lights

<hr>

Tries to algorithmically find an answer to the question, "What would be the ideal timings for this group of traffic signals?". Uses a Markov chain (the [metropolis filter](http://www.wikipedia.org/Metropolis_filter)) to traverse the state space of light timings, always edging closer to the optimal configuration to let the most people get to work on time.

Note that this project is never perfect and I'm always working on it, so don't look at this and see any kind of final product -- check back later and it'll be totally different!

<hr>

#### Tools Used

* Just [Scala](link) for this one, so far

<hr>

#### How to Run

Open up your favorite shell, and (assuming you've got the scala compiler):

```
$ scalac main.scala State.scala TrafficLights.scala
$ scala main
```