package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config/remote.properties"
})
public interface DriverConfig extends Config {
    @Key("browserRemoteUrl")
    String browserRemoteUrl();

}
