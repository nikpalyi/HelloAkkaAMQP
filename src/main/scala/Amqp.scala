import akka.actor.ActorSystem
import akka.stream.alpakka.amqp._
import akka.stream.alpakka.amqp.scaladsl.{AmqpFlow, AmqpSource}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import com.rabbitmq.client.ConnectionFactory
import com.typesafe.scalalogging.Logger

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object Amqp extends App {

  implicit val system = ActorSystem("QuickStart")
  val logger: Logger = Logger("hello_amqp")

  val connectionFactory = new ConnectionFactory()
  connectionFactory.setPort(5671)
  connectionFactory.setHost("localhost")
  connectionFactory.setPassword("docker")
  connectionFactory.setUsername("docker")

  val queueName = "nikiQ"
  val queueDeclaration = QueueDeclaration(queueName)
  val connectionProvider = AmqpConnectionFactoryConnectionProvider(factory = connectionFactory)

  val settings = AmqpWriteSettings(connectionProvider)
    .withRoutingKey(queueName)
    .withDeclaration(queueDeclaration)
    .withBufferSize(10)
    .withConfirmationTimeout(200.millis)

  val amqpFlow: Flow[WriteMessage, WriteResult, Future[Done]] =
    AmqpFlow.withConfirm(settings)

  val input = Vector("one", "two", "three", "four", "five")
  val result: Future[Seq[WriteResult]] =
    Source(input)
      .map(message => WriteMessage(ByteString(message)))
      .via(amqpFlow)
      .runWith(Sink.seq)

  val amqpSource: Source[ReadResult, NotUsed] =
    AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connectionProvider, queueName)
        .withDeclaration(queueDeclaration)
        .withAckRequired(false),
      bufferSize = 10
    )
}
