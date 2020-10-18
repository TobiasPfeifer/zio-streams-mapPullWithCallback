import zio.console.{Console, putStrLn}
import zio.stream.ZStream
import zio.{App, Chunk, ExitCode, URIO, ZIO, ZManaged}

object ZStreamMapPullWithCallbackDemo extends App {
  implicit class ZStreamMapPullWithCallback[-R, +E, +O](zStream: ZStream[R, E, O]) {
    def mapPullWithCallback[R1 <: R, E1 >: E, P](builderM: ZManaged[R1, Nothing, (() => Chunk[O]) => ZIO[R1, Option[E1], Chunk[P]]]): ZStream[R1, E1, P] = {
      ZStream {
        zStream.process.flatMap(eff => builderM.map(builder => eff.flatMap(chunk => builder(() => chunk))))
      }
    }
  }

  val demo: ZIO[Console, Nothing, Unit] = ZStream.range(0, 60, 3).mapPullWithCallback[Any, Nothing, Int] {
    val pullFromCallback = (callback: () => Chunk[Int]) => {
      val thirdPartyLibraryRequiresCallback = ThirdPartyLibraryRequiresCallback(() => callback().toList)
      ZIO.effectTotal(Chunk.fromIterable(thirdPartyLibraryRequiresCallback.getProcessedElements()))
    }
    ZManaged.succeed(pullFromCallback)
  }
  .runCollect
  .map(_.sorted)
  .flatMap(collected => putStrLn(collected.toString()))

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = demo.exitCode
}

/**
 * emulates 3rd party library class that requires a callback to pull more elements
 */
case class ThirdPartyLibraryRequiresCallback(callback: () => List[Int]) {
  def getProcessedElements(): List[Int] = callback().flatMap(i => List(i, i+60))
}
