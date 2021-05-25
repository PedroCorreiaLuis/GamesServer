package service.games.cardgames.utils

import org.scalacheck.Gen

import scala.language.implicitConversions

trait RandomizerImplicits {
  implicit def transformAny[T](g: Gen[T]): T = {
    g.sample.get
  }
}
