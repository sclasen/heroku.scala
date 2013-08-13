package com.heroku.platform.api.client.spray

import com.heroku.platform.api._

import play.api.libs.json._

object PlayJsonBoilerplate extends ApiRequestJson with ApiResponseJson {
  implicit lazy val ReadsUser: Reads[User] = {
    import com.heroku.platform.api.User
    Json.reads[User]
  }
  implicit lazy val ReadsHerokuAppmodelsAppRegion: Reads[HerokuApp.models.AppRegion] = {
    import com.heroku.platform.api.HerokuApp.models.AppRegion
    Json.reads[HerokuApp.models.AppRegion]
  }
  implicit lazy val ReadsHerokuAppmodelsAppOwner: Reads[HerokuApp.models.AppOwner] = {
    import com.heroku.platform.api.HerokuApp.models.AppOwner
    Json.reads[HerokuApp.models.AppOwner]
  }
  implicit lazy val ReadsHerokuApp: Reads[HerokuApp] = {
    import com.heroku.platform.api.HerokuApp
    Json.reads[HerokuApp]
  }
  implicit lazy val ReadsAccount: Reads[Account] = {
    import com.heroku.platform.api.Account
    Json.reads[Account]
  }
  implicit lazy val ReadsCollaborator: Reads[Collaborator] = {
    import com.heroku.platform.api.Collaborator
    Json.reads[Collaborator]
  }
  implicit lazy val ReadsCollaboratormodelsCollaboratedUser: Reads[Collaborator.models.CollaboratedUser] = {
    import com.heroku.platform.api.Collaborator.models.CollaboratedUser
    Json.reads[Collaborator.models.CollaboratedUser]
  }
  implicit lazy val ReadsDomain: Reads[Domain] = {
    import com.heroku.platform.api.Domain
    Json.reads[Domain]
  }
  implicit lazy val ReadsDynomodelsDynoRelease: Reads[Dyno.models.DynoRelease] = {
    import com.heroku.platform.api.Dyno.models.DynoRelease
    Json.reads[Dyno.models.DynoRelease]
  }
  implicit lazy val ReadsDyno: Reads[Dyno] = {
    import com.heroku.platform.api.Dyno
    Json.reads[Dyno]
  }
  implicit lazy val ReadsFormation: Reads[Formation] = {
    import com.heroku.platform.api.Formation
    Json.reads[Formation]
  }
  implicit lazy val ReadsKey: Reads[Key] = {
    import com.heroku.platform.api.Key
    Json.reads[Key]
  }
  implicit lazy val ReadsLogSession: Reads[LogSession] = {
    import com.heroku.platform.api.LogSession
    Json.reads[LogSession]
  }
  implicit lazy val ReadsRegion: Reads[Region] = {
    import com.heroku.platform.api.Region
    Json.reads[Region]
  }
  implicit lazy val ReadsRelease: Reads[Release] = {
    import com.heroku.platform.api.Release
    Json.reads[Release]
  }
  implicit lazy val ReadsOAuthAuthorization: Reads[OAuthAuthorization] = {
    import com.heroku.platform.api.OAuthAuthorization
    Json.reads[OAuthAuthorization]
  }
  implicit lazy val ReadsOAuthAuthorizationmodelsAccessToken: Reads[OAuthAuthorization.models.AccessToken] = {
    import com.heroku.platform.api.OAuthAuthorization.models.AccessToken
    Json.reads[OAuthAuthorization.models.AccessToken]
  }
  implicit lazy val ReadsOAuthAuthorizationmodelsClient: Reads[OAuthAuthorization.models.Client] = {
    import com.heroku.platform.api.OAuthAuthorization.models.Client
    Json.reads[OAuthAuthorization.models.Client]
  }
  implicit lazy val ReadsOAuthAuthorizationmodelsGrant: Reads[OAuthAuthorization.models.Grant] = {
    import com.heroku.platform.api.OAuthAuthorization.models.Grant
    Json.reads[OAuthAuthorization.models.Grant]
  }
  implicit lazy val ReadsOAuthAuthorizationmodelsRefreshToken: Reads[OAuthAuthorization.models.RefreshToken] = {
    import com.heroku.platform.api.OAuthAuthorization.models.RefreshToken
    Json.reads[OAuthAuthorization.models.RefreshToken]
  }
  implicit lazy val ReadsOAuthAuthorizationmodelsSession: Reads[OAuthAuthorization.models.Session] = {
    import com.heroku.platform.api.OAuthAuthorization.models.Session
    Json.reads[OAuthAuthorization.models.Session]
  }
  implicit lazy val ReadsOAuthClient: Reads[OAuthClient] = {
    import com.heroku.platform.api.OAuthClient
    Json.reads[OAuthClient]
  }
  implicit lazy val ReadsOAuthToken: Reads[OAuthToken] = {
    import com.heroku.platform.api.OAuthToken
    Json.reads[OAuthToken]
  }
  implicit lazy val ReadsOAuthTokenmodelsAccessToken: Reads[OAuthToken.models.AccessToken] = {
    import com.heroku.platform.api.OAuthToken.models.AccessToken
    Json.reads[OAuthToken.models.AccessToken]
  }
  implicit lazy val ReadsOAuthTokenmodelsAuthorization: Reads[OAuthToken.models.Authorization] = {
    import com.heroku.platform.api.OAuthToken.models.Authorization
    Json.reads[OAuthToken.models.Authorization]
  }
  implicit lazy val ReadsOAuthTokenmodelsRefreshToken: Reads[OAuthToken.models.RefreshToken] = {
    import com.heroku.platform.api.OAuthToken.models.RefreshToken
    Json.reads[OAuthToken.models.RefreshToken]
  }
  implicit lazy val ReadsAppTransferApp: Reads[AppTransfer.App] = {
    import com.heroku.platform.api.AppTransfer.App
    Json.reads[AppTransfer.App]
  }
  implicit lazy val ReadsAppTransfer: Reads[AppTransfer] = {
    import com.heroku.platform.api.AppTransfer
    Json.reads[AppTransfer]
  }
  implicit lazy val ReadsAddonmodelsAddonPlan: Reads[Addon.models.AddonPlan] = {
    import com.heroku.platform.api.Addon.models.AddonPlan
    Json.reads[Addon.models.AddonPlan]
  }
  implicit lazy val ReadsAddon: Reads[Addon] = {
    import com.heroku.platform.api.Addon
    Json.reads[Addon]
  }
  implicit lazy val ReadsErrorResponse: Reads[ErrorResponse] = {
    import com.heroku.platform.api.ErrorResponse
    Json.reads[ErrorResponse]
  }
  implicit lazy val WritesUserBody: Writes[UserBody] = {
    import com.heroku.platform.api.UserBody
    Json.writes[UserBody]
  }
  implicit lazy val WritesAccountUpdateBody: Writes[Account.UpdateBody] = {
    import com.heroku.platform.api.Account.UpdateBody
    Json.writes[Account.UpdateBody]
  }
  implicit lazy val WritesAccountPasswordChangeBody: Writes[Account.PasswordChangeBody] = {
    import com.heroku.platform.api.Account.PasswordChangeBody
    Json.writes[Account.PasswordChangeBody]
  }
  implicit lazy val WritesHerokuAppmodelsAppRegion: Writes[HerokuApp.models.AppRegion] = {
    import com.heroku.platform.api.HerokuApp.models.AppRegion
    Json.writes[HerokuApp.models.AppRegion]
  }
  implicit lazy val WritesHerokuAppmodelsCreateAppBody: Writes[HerokuApp.models.CreateAppBody] = {
    import com.heroku.platform.api.HerokuApp.models.CreateAppBody
    Json.writes[HerokuApp.models.CreateAppBody]
  }
  implicit lazy val WritesHerokuAppmodelsUpdateAppBody: Writes[HerokuApp.models.UpdateAppBody] = {
    import com.heroku.platform.api.HerokuApp.models.UpdateAppBody
    Json.writes[HerokuApp.models.UpdateAppBody]
  }
  implicit lazy val WritesHerokuAppmodelsAppOwner: Writes[HerokuApp.models.AppOwner] = {
    import com.heroku.platform.api.HerokuApp.models.AppOwner
    Json.writes[HerokuApp.models.AppOwner]
  }
  implicit lazy val WritesCollaboratormodelsCollaboratorBody: Writes[Collaborator.models.CollaboratorBody] = {
    import com.heroku.platform.api.Collaborator.models.CollaboratorBody
    Json.writes[Collaborator.models.CollaboratorBody]
  }
  implicit lazy val WritesDomainCreateDomainBody: Writes[Domain.CreateDomainBody] = {
    import com.heroku.platform.api.Domain.CreateDomainBody
    Json.writes[Domain.CreateDomainBody]
  }
  implicit lazy val WritesDynomodelsCreateDynoBody: Writes[Dyno.models.CreateDynoBody] = {
    import com.heroku.platform.api.Dyno.models.CreateDynoBody
    Json.writes[Dyno.models.CreateDynoBody]
  }
  implicit lazy val WritesFormationUpdateFormationBody: Writes[Formation.UpdateFormationBody] = {
    import com.heroku.platform.api.Formation.UpdateFormationBody
    Json.writes[Formation.UpdateFormationBody]
  }
  implicit lazy val WritesKeyCreateKeyBody: Writes[Key.CreateKeyBody] = {
    import com.heroku.platform.api.Key.CreateKeyBody
    Json.writes[Key.CreateKeyBody]
  }
  implicit lazy val WritesOAuthAuthorizationmodelsCreateAuthorizationClient: Writes[OAuthAuthorization.models.CreateAuthorizationClient] = {
    import com.heroku.platform.api.OAuthAuthorization.models.CreateAuthorizationClient
    Json.writes[OAuthAuthorization.models.CreateAuthorizationClient]
  }
  implicit lazy val WritesOAuthAuthorizationmodelsCreateAuthorizationBody: Writes[OAuthAuthorization.models.CreateAuthorizationBody] = {
    import com.heroku.platform.api.OAuthAuthorization.models.CreateAuthorizationBody
    Json.writes[OAuthAuthorization.models.CreateAuthorizationBody]
  }
  implicit lazy val WritesAppTransferState: Writes[AppTransfer.State] = {
    import com.heroku.platform.api.AppTransfer.State
    Json.writes[AppTransfer.State]
  }
  implicit lazy val WritesAppTransferApp: Writes[AppTransfer.App] = {
    import com.heroku.platform.api.AppTransfer.App
    Json.writes[AppTransfer.App]
  }
  implicit lazy val WritesAppTransferCreateTransferBody: Writes[AppTransfer.CreateTransferBody] = {
    import com.heroku.platform.api.AppTransfer.CreateTransferBody
    Json.writes[AppTransfer.CreateTransferBody]
  }
  implicit lazy val WritesAddonmodelsAddonChange: Writes[Addon.models.AddonChange] = {
    import com.heroku.platform.api.Addon.models.AddonChange
    Json.writes[Addon.models.AddonChange]
  }
  implicit lazy val WritesAddonmodelsAddonPlan: Writes[Addon.models.AddonPlan] = {
    import com.heroku.platform.api.Addon.models.AddonPlan
    Json.writes[Addon.models.AddonPlan]
  }
  implicit lazy val userBodyToJson: ToJson[UserBody] = to[UserBody]
  implicit lazy val updateAccountToJson: ToJson[Account.UpdateBody] = to[Account.UpdateBody]
  implicit lazy val passwordChangeToJson: ToJson[Account.PasswordChangeBody] = to[Account.PasswordChangeBody]
  implicit lazy val appRegionToJson: ToJson[HerokuApp.models.AppRegion] = to[HerokuApp.models.AppRegion]
  implicit lazy val createAppBodyToJson: ToJson[HerokuApp.models.CreateAppBody] = to[HerokuApp.models.CreateAppBody]
  implicit lazy val updateAppBodyToJson: ToJson[HerokuApp.models.UpdateAppBody] = to[HerokuApp.models.UpdateAppBody]
  implicit lazy val appOwnerToJson: ToJson[HerokuApp.models.AppOwner] = to[HerokuApp.models.AppOwner]
  implicit lazy val collaboratorBodyToJson: ToJson[Collaborator.models.CollaboratorBody] = to[Collaborator.models.CollaboratorBody]
  implicit lazy val configToJson: ToJson[Map[String,String]] = to[Map[String,String]]
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
  implicit lazy val userFromJson: FromJson[User] = from[User]
  implicit lazy val appRegionFromJson: FromJson[HerokuApp.models.AppRegion] = from[HerokuApp.models.AppRegion]
  implicit lazy val appOwnerFromJson: FromJson[HerokuApp.models.AppOwner] = from[HerokuApp.models.AppOwner]
  implicit lazy val appFromJson: FromJson[HerokuApp] = from[HerokuApp]
  implicit lazy val appListFromJson: FromJson[collection.immutable.List[HerokuApp]] = from[collection.immutable.List[HerokuApp]]
  implicit lazy val accountFromJson: FromJson[Account] = from[Account]
  implicit lazy val collaboratorFromJson: FromJson[Collaborator] = from[Collaborator]
  implicit lazy val collaboratorListFromJson: FromJson[collection.immutable.List[Collaborator]] = from[collection.immutable.List[Collaborator]]
  implicit lazy val collaboratedUserFromJson: FromJson[Collaborator.models.CollaboratedUser] = from[Collaborator.models.CollaboratedUser]
  implicit lazy val configFromJson: FromJson[Map[String,String]] = from[Map[String,String]]
  implicit lazy val domainFromJson: FromJson[Domain] = from[Domain]
  implicit lazy val domainListFromJson: FromJson[collection.immutable.List[Domain]] = from[collection.immutable.List[Domain]]
  implicit lazy val dynoReleaseFromJson: FromJson[Dyno.models.DynoRelease] = from[Dyno.models.DynoRelease]
  implicit lazy val dynoFromJson: FromJson[Dyno] = from[Dyno]
  implicit lazy val formationFromJson: FromJson[Formation] = from[Formation]
  implicit lazy val formationListFromJson: FromJson[collection.immutable.List[Formation]] = from[collection.immutable.List[Formation]]
  implicit lazy val keyFromJson: FromJson[Key] = from[Key]
  implicit lazy val logSessionFromJson: FromJson[LogSession] = from[LogSession]
  implicit lazy val regionListFromJson: FromJson[collection.immutable.List[Region]] = from[collection.immutable.List[Region]]
  implicit lazy val regionFromJson: FromJson[Region] = from[Region]
  implicit lazy val releaseFromJson: FromJson[Release] = from[Release]
  implicit lazy val releaseListFromJson: FromJson[collection.immutable.List[Release]] = from[collection.immutable.List[Release]]
  implicit lazy val oauthAuthorizationFromJson: FromJson[OAuthAuthorization] = from[OAuthAuthorization]
  implicit lazy val oauthAuthorizationAccessTokenFromJson: FromJson[OAuthAuthorization.models.AccessToken] = from[OAuthAuthorization.models.AccessToken]
  implicit lazy val oauthAuthorizationClientFromJson: FromJson[OAuthAuthorization.models.Client] = from[OAuthAuthorization.models.Client]
  implicit lazy val oauthAuthorizationGrantFromJson: FromJson[OAuthAuthorization.models.Grant] = from[OAuthAuthorization.models.Grant]
  implicit lazy val oauthAuthorizationRefreshTokenFromJson: FromJson[OAuthAuthorization.models.RefreshToken] = from[OAuthAuthorization.models.RefreshToken]
  implicit lazy val oauthAuthorizationSessionFromJson: FromJson[OAuthAuthorization.models.Session] = from[OAuthAuthorization.models.Session]
  implicit lazy val oauthAuthorizationListFromJson: FromJson[collection.immutable.List[OAuthAuthorization]] = from[collection.immutable.List[OAuthAuthorization]]
  implicit lazy val oauthClientFromJson: FromJson[OAuthClient] = from[OAuthClient]
  implicit lazy val oauthClientListFromJson: FromJson[collection.immutable.List[OAuthClient]] = from[collection.immutable.List[OAuthClient]]
  implicit lazy val oauthTokenFromJson: FromJson[OAuthToken] = from[OAuthToken]
  implicit lazy val oauthTokenAccessTokenFromJson: FromJson[OAuthToken.models.AccessToken] = from[OAuthToken.models.AccessToken]
  implicit lazy val oauthTokenAuthorizationFromJson: FromJson[OAuthToken.models.Authorization] = from[OAuthToken.models.Authorization]
  implicit lazy val oauthTokenRefreshTokenFromJson: FromJson[OAuthToken.models.RefreshToken] = from[OAuthToken.models.RefreshToken]
  implicit lazy val oauthTokenListFromJson: FromJson[collection.immutable.List[OAuthToken]] = from[collection.immutable.List[OAuthToken]]
  implicit lazy val appTransferAppFromJson: FromJson[AppTransfer.App] = from[AppTransfer.App]
  implicit lazy val appTransferFromJson: FromJson[AppTransfer] = from[AppTransfer]
  implicit lazy val appTransferListFromJson: FromJson[collection.immutable.List[AppTransfer]] = from[collection.immutable.List[AppTransfer]]
  implicit lazy val addonPlanFromJson: FromJson[Addon.models.AddonPlan] = from[Addon.models.AddonPlan]
  implicit lazy val addonFromJson: FromJson[Addon] = from[Addon]
  implicit lazy val addonListFromJson: FromJson[collection.immutable.List[Addon]] = from[collection.immutable.List[Addon]]
  implicit lazy val errorResponseFromJson: FromJson[ErrorResponse] = from[ErrorResponse]
  def from[T](implicit t: Reads[T]) = new FromJson[T] {
    def fromJson(json: String) = Json.parse(json).as[T]
  }
  def to[T](implicit f: Writes[T]) = new ToJson[T] {
    def toJson(t: T) = Json.prettyPrint(Json.toJson(t))
  }
}
