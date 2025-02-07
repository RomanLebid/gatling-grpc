package com.github.phisgr.gatling.grpc.action

import com.github.phisgr.gatling.forToMatch
import com.github.phisgr.gatling.grpc.stream.StreamCall
import com.github.phisgr.gatling.grpc.stream.StreamCall.WaitType
import io.gatling.commons.validation.Validation
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext

class StreamReconciliateBuilder(
  requestName: Expression[String],
  streamName: String,
  direction: String,
  waitType: WaitType
) extends ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action =
    new StreamMessageAction(requestName, ctx, next, baseName = "StreamReconciliate", direction) {
      override def sendRequest(requestName: String, session: Session): Validation[Unit] = forToMatch {
        for {
          call <- fetchCall[StreamCall[_, _, _]](streamName, session)
        } yield {
          call.combineState(session, next, waitType)
        }
      }
    }
}
