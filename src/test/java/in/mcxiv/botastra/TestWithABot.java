package in.mcxiv.botastra;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

// A bare minimum bot to test shit
// use https://github.com/Xirado/Bean as a reference
class TestWithABot {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(Platform.class.getClassLoader().getResourceAsStream("app.properties"));
            String token = properties.getProperty("bot-token");

            JDA jda = JDABuilder.createDefault(token)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing("no game..."))
                    .build();

            Platform.initializeFor(jda);

        } catch (IOException | LoginException e) {
            e.printStackTrace();
        }
    }

}