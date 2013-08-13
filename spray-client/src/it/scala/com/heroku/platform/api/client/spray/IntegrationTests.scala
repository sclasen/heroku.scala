package com.heroku.platform.api.client.spray

class SprayAccountSpec extends AccountSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayAccountSpec extends AccountSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayAddonSpec extends AddonSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayAddonSpec extends AddonSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayAppSpec extends AppSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayAppSpec extends AppSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayAppTransferSpec extends AppTransferSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayAppTransferSpec extends AppTransferSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayCollaboratorSpec extends CollaboratorSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayCollaboratorSpec extends CollaboratorSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayConfigVarSpec extends ConfigVarSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayConfigVarSpec extends ConfigVarSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayDomainSpec extends DomainSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayDomainSpec extends DomainSpec(PlayJsonBoilerplate) with SprayApiSpec

class SprayOAuthSpec extends OAuthSpec(SprayJsonBoilerplate) with SprayApiSpec

class PlayOAuthSpec extends OAuthSpec(PlayJsonBoilerplate) with SprayApiSpec









