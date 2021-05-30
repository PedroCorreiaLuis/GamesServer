package api.controllers

import play.api.libs.json._

object JsonObjectsUtils {

  def jsonErrors(errors: scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])]): JsObject = {

    Json.obj("Invalid JSON" -> JsError.toJson(errors))
  }
}
