package com.heroku.platform.api

case class User(id: String, email: String)

case class UserBody(id: Option[String] = None, email: Option[String] = None)

trait ErrorResponseJson {
  implicit def errorResponseFromJson: FromJson[ErrorResponse]
}

trait ApiResponseJson extends HerokuAppResponseJson with AccountResponseJson with CollaboratorResponseJson
    with ConfigVarResponseJson with DomainResponseJson with DynoResponseJson with FormationResponseJson
    with KeyResponseJson with LogSessionResponseJson with RegionResponseJson with ReleaseResponseJson
    with OAuthResponseJson with AppTransferResponseJson with AddonResponseJson with ErrorResponseJson {
  implicit def userFromJson: FromJson[User]
}

trait ApiRequestJson extends AccountRequestJson with HerokuAppRequestJson with CollaboratorRequestJson with ConfigVarRequestJson
    with DomainRequestJson with DynoRequestJson with FormationRequestJson with KeyRequestJson with OAuthRequestJson with AppTransferRequestJson
    with AddonRequestJson {
  implicit def userBodyToJson: ToJson[UserBody]
}
