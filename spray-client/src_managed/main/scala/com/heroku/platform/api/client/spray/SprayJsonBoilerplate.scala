package com.heroku.platform.api.client.spray

import com.heroku.platform.api._

import spray.json._

object SprayJsonIgnoreNullBoilerplate extends DefaultJsonProtocol with ApiRequestJson {
  implicit lazy val nullSafeConfigToJson: ToJson[Map[String, Option[String]]] = to[Map[String, Option[String]]]
  implicit lazy val configToJson: ToJson[Map[String, String]] = new ToJson[Map[String, String]] {
    def toJson(t: Map[String, String]) = nullSafeConfigToJson.toJson(t map {
      case (k, v) => k -> Option(v)
    })
  }
  implicit lazy val FormatUserBody: JsonFormat[UserBody] = jsonFormat2(UserBody.apply)
  implicit lazy val FormatAccountUpdateBody: JsonFormat[Account.UpdateBody] = jsonFormat3(Account.UpdateBody.apply)
  implicit lazy val FormatAccountPasswordChangeBody: JsonFormat[Account.PasswordChangeBody] = jsonFormat2(Account.PasswordChangeBody.apply)
  implicit lazy val FormatHerokuAppmodelsAppRegion: JsonFormat[HerokuApp.models.AppRegion] = jsonFormat2(HerokuApp.models.AppRegion.apply)
  implicit lazy val FormatHerokuAppmodelsCreateAppBody: JsonFormat[HerokuApp.models.CreateAppBody] = jsonFormat3(HerokuApp.models.CreateAppBody.apply)
  implicit lazy val FormatHerokuAppmodelsUpdateAppBody: JsonFormat[HerokuApp.models.UpdateAppBody] = jsonFormat3(HerokuApp.models.UpdateAppBody.apply)
  implicit lazy val FormatHerokuAppmodelsAppOwner: JsonFormat[HerokuApp.models.AppOwner] = jsonFormat2(HerokuApp.models.AppOwner.apply)
  implicit lazy val FormatCollaboratormodelsCollaboratorBody: JsonFormat[Collaborator.models.CollaboratorBody] = jsonFormat1(Collaborator.models.CollaboratorBody.apply)
  implicit lazy val FormatDomainCreateDomainBody: JsonFormat[Domain.CreateDomainBody] = jsonFormat1(Domain.CreateDomainBody.apply)
  implicit lazy val FormatDynomodelsCreateDynoBody: JsonFormat[Dyno.models.CreateDynoBody] = jsonFormat2(Dyno.models.CreateDynoBody.apply)
  implicit lazy val FormatFormationUpdateFormationBody: JsonFormat[Formation.UpdateFormationBody] = jsonFormat1(Formation.UpdateFormationBody.apply)
  implicit lazy val FormatKeyCreateKeyBody: JsonFormat[Key.CreateKeyBody] = jsonFormat1(Key.CreateKeyBody.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsCreateAuthorizationClient: JsonFormat[OAuthAuthorization.models.CreateAuthorizationClient] = jsonFormat1(OAuthAuthorization.models.CreateAuthorizationClient.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsCreateAuthorizationBody: JsonFormat[OAuthAuthorization.models.CreateAuthorizationBody] = jsonFormat3(OAuthAuthorization.models.CreateAuthorizationBody.apply)
  implicit lazy val FormatAppTransferState: JsonFormat[AppTransfer.State] = jsonFormat1(AppTransfer.State.apply)
  implicit lazy val FormatAppTransferApp: JsonFormat[AppTransfer.App] = jsonFormat2(AppTransfer.App.apply)
  implicit lazy val FormatAppTransferCreateTransferBody: JsonFormat[AppTransfer.CreateTransferBody] = jsonFormat2(AppTransfer.CreateTransferBody.apply)
  implicit lazy val FormatAddonmodelsAddonChange: JsonFormat[Addon.models.AddonChange] = jsonFormat2(Addon.models.AddonChange.apply)
  implicit lazy val FormatAddonmodelsAddonPlan: JsonFormat[Addon.models.AddonPlan] = jsonFormat1(Addon.models.AddonPlan.apply)
  implicit lazy val userBodyToJson: ToJson[UserBody] = to[UserBody]
  implicit lazy val updateAccountToJson: ToJson[Account.UpdateBody] = to[Account.UpdateBody]
  implicit lazy val passwordChangeToJson: ToJson[Account.PasswordChangeBody] = to[Account.PasswordChangeBody]
  implicit lazy val appRegionToJson: ToJson[HerokuApp.models.AppRegion] = to[HerokuApp.models.AppRegion]
  implicit lazy val createAppBodyToJson: ToJson[HerokuApp.models.CreateAppBody] = to[HerokuApp.models.CreateAppBody]
  implicit lazy val updateAppBodyToJson: ToJson[HerokuApp.models.UpdateAppBody] = to[HerokuApp.models.UpdateAppBody]
  implicit lazy val appOwnerToJson: ToJson[HerokuApp.models.AppOwner] = to[HerokuApp.models.AppOwner]
  implicit lazy val collaboratorBodyToJson: ToJson[Collaborator.models.CollaboratorBody] = to[Collaborator.models.CollaboratorBody]
  implicit lazy val createDomainBodyToJson: ToJson[Domain.CreateDomainBody] = to[Domain.CreateDomainBody]
  implicit lazy val createDynoBodyToJson: ToJson[Dyno.models.CreateDynoBody] = to[Dyno.models.CreateDynoBody]
  implicit lazy val updateFormationBodyToJson: ToJson[Formation.UpdateFormationBody] = to[Formation.UpdateFormationBody]
  implicit lazy val createKeyBodyToJson: ToJson[Key.CreateKeyBody] = to[Key.CreateKeyBody]
  implicit lazy val oauthCreateAuthoriztionClient: ToJson[OAuthAuthorization.models.CreateAuthorizationClient] = to[OAuthAuthorization.models.CreateAuthorizationClient]
  implicit lazy val oauthcreateAuthorizationBody: ToJson[OAuthAuthorization.models.CreateAuthorizationBody] = to[OAuthAuthorization.models.CreateAuthorizationBody]
  implicit lazy val stateToJson: ToJson[AppTransfer.State] = to[AppTransfer.State]
  implicit lazy val appTransferAppToJson: ToJson[AppTransfer.App] = to[AppTransfer.App]
  implicit lazy val createTransferBodyToJson: ToJson[AppTransfer.CreateTransferBody] = to[AppTransfer.CreateTransferBody]
  implicit lazy val addonChangeToJson: ToJson[Addon.models.AddonChange] = to[Addon.models.AddonChange]
  implicit lazy val addonPlanToJson: ToJson[Addon.models.AddonPlan] = to[Addon.models.AddonPlan]
  def to[T](implicit f: JsonFormat[T]) = new ToJson[T] {
    def toJson(t: T) = t.toJson.compactPrint
  }
}

object SprayJsonBoilerplate extends DefaultJsonProtocol with NullOptions with ApiRequestJson with ApiResponseJson {
  implicit lazy val configToJson: ToJson[Map[String, String]] = SprayJsonIgnoreNullBoilerplate.configToJson
  implicit lazy val FormatUser: JsonFormat[User] = jsonFormat2(User.apply)
  implicit lazy val FormatHerokuAppmodelsAppRegion: JsonFormat[HerokuApp.models.AppRegion] = jsonFormat2(HerokuApp.models.AppRegion.apply)
  implicit lazy val FormatHerokuAppmodelsAppOwner: JsonFormat[HerokuApp.models.AppOwner] = jsonFormat2(HerokuApp.models.AppOwner.apply)
  implicit lazy val FormatHerokuApp: JsonFormat[HerokuApp] = jsonFormat15(HerokuApp.apply)
  implicit lazy val FormatAccount: JsonFormat[Account] = jsonFormat8(Account.apply)
  implicit lazy val FormatCollaborator: JsonFormat[Collaborator] = jsonFormat3(Collaborator.apply)
  implicit lazy val FormatCollaboratormodelsCollaboratedUser: JsonFormat[Collaborator.models.CollaboratedUser] = jsonFormat2(Collaborator.models.CollaboratedUser.apply)
  implicit lazy val FormatDomain: JsonFormat[Domain] = jsonFormat4(Domain.apply)
  implicit lazy val FormatDynomodelsDynoRelease: JsonFormat[Dyno.models.DynoRelease] = jsonFormat1(Dyno.models.DynoRelease.apply)
  implicit lazy val FormatDyno: JsonFormat[Dyno] = jsonFormat9(Dyno.apply)
  implicit lazy val FormatFormation: JsonFormat[Formation] = jsonFormat5(Formation.apply)
  implicit lazy val FormatKey: JsonFormat[Key] = jsonFormat5(Key.apply)
  implicit lazy val FormatLogSession: JsonFormat[LogSession] = jsonFormat2(LogSession.apply)
  implicit lazy val FormatRegion: JsonFormat[Region] = jsonFormat5(Region.apply)
  implicit lazy val FormatRelease: JsonFormat[Release] = jsonFormat6(Release.apply)
  implicit lazy val FormatOAuthAuthorization: JsonFormat[OAuthAuthorization] = jsonFormat9(OAuthAuthorization.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsAccessToken: JsonFormat[OAuthAuthorization.models.AccessToken] = jsonFormat3(OAuthAuthorization.models.AccessToken.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsClient: JsonFormat[OAuthAuthorization.models.Client] = jsonFormat3(OAuthAuthorization.models.Client.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsGrant: JsonFormat[OAuthAuthorization.models.Grant] = jsonFormat3(OAuthAuthorization.models.Grant.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsRefreshToken: JsonFormat[OAuthAuthorization.models.RefreshToken] = jsonFormat3(OAuthAuthorization.models.RefreshToken.apply)
  implicit lazy val FormatOAuthAuthorizationmodelsSession: JsonFormat[OAuthAuthorization.models.Session] = jsonFormat1(OAuthAuthorization.models.Session.apply)
  implicit lazy val FormatOAuthClient: JsonFormat[OAuthClient] = jsonFormat6(OAuthClient.apply)
  implicit lazy val FormatOAuthToken: JsonFormat[OAuthToken] = jsonFormat7(OAuthToken.apply)
  implicit lazy val FormatOAuthTokenmodelsAccessToken: JsonFormat[OAuthToken.models.AccessToken] = jsonFormat3(OAuthToken.models.AccessToken.apply)
  implicit lazy val FormatOAuthTokenmodelsAuthorization: JsonFormat[OAuthToken.models.Authorization] = jsonFormat1(OAuthToken.models.Authorization.apply)
  implicit lazy val FormatOAuthTokenmodelsRefreshToken: JsonFormat[OAuthToken.models.RefreshToken] = jsonFormat3(OAuthToken.models.RefreshToken.apply)
  implicit lazy val FormatAppTransferApp: JsonFormat[AppTransfer.App] = jsonFormat2(AppTransfer.App.apply)
  implicit lazy val FormatAppTransfer: JsonFormat[AppTransfer] = jsonFormat7(AppTransfer.apply)
  implicit lazy val FormatAddonmodelsAddonPlan: JsonFormat[Addon.models.AddonPlan] = jsonFormat1(Addon.models.AddonPlan.apply)
  implicit lazy val FormatAddon: JsonFormat[Addon] = jsonFormat5(Addon.apply)
  implicit lazy val FormatErrorResponse: JsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse.apply)
  implicit lazy val userBodyToJson: ToJson[UserBody] = SprayJsonIgnoreNullBoilerplate.userBodyToJson
  implicit lazy val updateAccountToJson: ToJson[Account.UpdateBody] = SprayJsonIgnoreNullBoilerplate.updateAccountToJson
  implicit lazy val passwordChangeToJson: ToJson[Account.PasswordChangeBody] = SprayJsonIgnoreNullBoilerplate.passwordChangeToJson
  implicit lazy val appRegionToJson: ToJson[HerokuApp.models.AppRegion] = SprayJsonIgnoreNullBoilerplate.appRegionToJson
  implicit lazy val createAppBodyToJson: ToJson[HerokuApp.models.CreateAppBody] = SprayJsonIgnoreNullBoilerplate.createAppBodyToJson
  implicit lazy val updateAppBodyToJson: ToJson[HerokuApp.models.UpdateAppBody] = SprayJsonIgnoreNullBoilerplate.updateAppBodyToJson
  implicit lazy val appOwnerToJson: ToJson[HerokuApp.models.AppOwner] = SprayJsonIgnoreNullBoilerplate.appOwnerToJson
  implicit lazy val collaboratorBodyToJson: ToJson[Collaborator.models.CollaboratorBody] = SprayJsonIgnoreNullBoilerplate.collaboratorBodyToJson
  implicit lazy val createDomainBodyToJson: ToJson[Domain.CreateDomainBody] = SprayJsonIgnoreNullBoilerplate.createDomainBodyToJson
  implicit lazy val createDynoBodyToJson: ToJson[Dyno.models.CreateDynoBody] = SprayJsonIgnoreNullBoilerplate.createDynoBodyToJson
  implicit lazy val updateFormationBodyToJson: ToJson[Formation.UpdateFormationBody] = SprayJsonIgnoreNullBoilerplate.updateFormationBodyToJson
  implicit lazy val createKeyBodyToJson: ToJson[Key.CreateKeyBody] = SprayJsonIgnoreNullBoilerplate.createKeyBodyToJson
  implicit lazy val oauthCreateAuthoriztionClient: ToJson[OAuthAuthorization.models.CreateAuthorizationClient] = SprayJsonIgnoreNullBoilerplate.oauthCreateAuthoriztionClient
  implicit lazy val oauthcreateAuthorizationBody: ToJson[OAuthAuthorization.models.CreateAuthorizationBody] = SprayJsonIgnoreNullBoilerplate.oauthcreateAuthorizationBody
  implicit lazy val stateToJson: ToJson[AppTransfer.State] = SprayJsonIgnoreNullBoilerplate.stateToJson
  implicit lazy val appTransferAppToJson: ToJson[AppTransfer.App] = SprayJsonIgnoreNullBoilerplate.appTransferAppToJson
  implicit lazy val createTransferBodyToJson: ToJson[AppTransfer.CreateTransferBody] = SprayJsonIgnoreNullBoilerplate.createTransferBodyToJson
  implicit lazy val addonChangeToJson: ToJson[Addon.models.AddonChange] = SprayJsonIgnoreNullBoilerplate.addonChangeToJson
  implicit lazy val addonPlanToJson: ToJson[Addon.models.AddonPlan] = SprayJsonIgnoreNullBoilerplate.addonPlanToJson
  implicit lazy val userFromJson: FromJson[User] = from[User]
  implicit lazy val appRegionFromJson: FromJson[HerokuApp.models.AppRegion] = from[HerokuApp.models.AppRegion]
  implicit lazy val appOwnerFromJson: FromJson[HerokuApp.models.AppOwner] = from[HerokuApp.models.AppOwner]
  implicit lazy val appFromJson: FromJson[HerokuApp] = from[HerokuApp]
  implicit lazy val appListFromJson: FromJson[List[HerokuApp]] = from[List[HerokuApp]]
  implicit lazy val accountFromJson: FromJson[Account] = from[Account]
  implicit lazy val collaboratorFromJson: FromJson[Collaborator] = from[Collaborator]
  implicit lazy val collaboratorListFromJson: FromJson[List[Collaborator]] = from[List[Collaborator]]
  implicit lazy val collaboratedUserFromJson: FromJson[Collaborator.models.CollaboratedUser] = from[Collaborator.models.CollaboratedUser]
  implicit lazy val configFromJson: FromJson[Map[String,String]] = from[Map[String,String]]
  implicit lazy val domainFromJson: FromJson[Domain] = from[Domain]
  implicit lazy val domainListFromJson: FromJson[List[Domain]] = from[List[Domain]]
  implicit lazy val dynoReleaseFromJson: FromJson[Dyno.models.DynoRelease] = from[Dyno.models.DynoRelease]
  implicit lazy val dynoFromJson: FromJson[Dyno] = from[Dyno]
  implicit lazy val formationFromJson: FromJson[Formation] = from[Formation]
  implicit lazy val formationListFromJson: FromJson[List[Formation]] = from[List[Formation]]
  implicit lazy val keyFromJson: FromJson[Key] = from[Key]
  implicit lazy val logSessionFromJson: FromJson[LogSession] = from[LogSession]
  implicit lazy val regionListFromJson: FromJson[List[Region]] = from[List[Region]]
  implicit lazy val regionFromJson: FromJson[Region] = from[Region]
  implicit lazy val releaseFromJson: FromJson[Release] = from[Release]
  implicit lazy val releaseListFromJson: FromJson[List[Release]] = from[List[Release]]
  implicit lazy val oauthAuthorizationFromJson: FromJson[OAuthAuthorization] = from[OAuthAuthorization]
  implicit lazy val oauthAuthorizationAccessTokenFromJson: FromJson[OAuthAuthorization.models.AccessToken] = from[OAuthAuthorization.models.AccessToken]
  implicit lazy val oauthAuthorizationClientFromJson: FromJson[OAuthAuthorization.models.Client] = from[OAuthAuthorization.models.Client]
  implicit lazy val oauthAuthorizationGrantFromJson: FromJson[OAuthAuthorization.models.Grant] = from[OAuthAuthorization.models.Grant]
  implicit lazy val oauthAuthorizationRefreshTokenFromJson: FromJson[OAuthAuthorization.models.RefreshToken] = from[OAuthAuthorization.models.RefreshToken]
  implicit lazy val oauthAuthorizationSessionFromJson: FromJson[OAuthAuthorization.models.Session] = from[OAuthAuthorization.models.Session]
  implicit lazy val oauthAuthorizationListFromJson: FromJson[List[OAuthAuthorization]] = from[List[OAuthAuthorization]]
  implicit lazy val oauthClientFromJson: FromJson[OAuthClient] = from[OAuthClient]
  implicit lazy val oauthClientListFromJson: FromJson[List[OAuthClient]] = from[List[OAuthClient]]
  implicit lazy val oauthTokenFromJson: FromJson[OAuthToken] = from[OAuthToken]
  implicit lazy val oauthTokenAccessTokenFromJson: FromJson[OAuthToken.models.AccessToken] = from[OAuthToken.models.AccessToken]
  implicit lazy val oauthTokenAuthorizationFromJson: FromJson[OAuthToken.models.Authorization] = from[OAuthToken.models.Authorization]
  implicit lazy val oauthTokenRefreshTokenFromJson: FromJson[OAuthToken.models.RefreshToken] = from[OAuthToken.models.RefreshToken]
  implicit lazy val oauthTokenListFromJson: FromJson[List[OAuthToken]] = from[List[OAuthToken]]
  implicit lazy val appTransferAppFromJson: FromJson[AppTransfer.App] = from[AppTransfer.App]
  implicit lazy val appTransferFromJson: FromJson[AppTransfer] = from[AppTransfer]
  implicit lazy val appTransferListFromJson: FromJson[List[AppTransfer]] = from[List[AppTransfer]]
  implicit lazy val addonPlanFromJson: FromJson[Addon.models.AddonPlan] = from[Addon.models.AddonPlan]
  implicit lazy val addonFromJson: FromJson[Addon] = from[Addon]
  implicit lazy val addonListFromJson: FromJson[List[Addon]] = from[List[Addon]]
  implicit lazy val errorResponseFromJson: FromJson[ErrorResponse] = from[ErrorResponse]
  def from[T](implicit t: JsonFormat[T]) = new FromJson[T] {
    def fromJson(json: String) =
      try {
        JsonParser(json).convertTo[T]
      } catch {
        case (e: DeserializationException) => {
          println(json)
          throw new DeserializationException(e.toString)
        }
      }
  }
}
