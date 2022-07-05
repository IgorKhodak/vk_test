package ru.vk.userProvider;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.UserAuthResponse;

public class UserProvider {

    public static UserActor getUserActor(UserAuthResponse auth) {
        return new UserActor(auth.getUserId(), auth.getAccessToken());
    }

}
