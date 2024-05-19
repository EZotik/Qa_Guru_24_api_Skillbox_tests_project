package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/credential.properties"
})
public interface CredentialConfig extends Config {
    @Key("name.userName")
    String loginUserName();

    @Key("name.password")
    String loginPassword();

}
