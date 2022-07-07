package ru.vk.likes;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAccessException;
import com.vk.api.sdk.exceptions.ApiParamException;
import com.vk.api.sdk.exceptions.ApiPrivateProfileException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.objects.likes.Type;
import com.vk.api.sdk.objects.likes.responses.AddResponse;
import com.vk.api.sdk.objects.likes.responses.DeleteResponse;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.vk.AppConfig;
import ru.vk.dataProvider.LikesDataProvider;
import ru.vk.userProvider.UserProvider;

import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AppConfig.class)
public class LikesTest extends AbstractTestNGSpringContextTests {

    private static final int DEFAULT_ITEM_ID = NumberUtils.INTEGER_ONE;

    @Autowired
    private VkApiClient vkApi;

    @Autowired
    private UserAuthResponse authResponse;

    @Test(
            description = "Add item to current users likes list",
            groups = {"smoke", "likes"},
            priority = 0
    )
    void addLikeToPostTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            AddResponse actualResponse = vkApi.likes()
                    .add(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(actor.getId())
                    .execute();

            assertThat(actualResponse.getLikes())
                    .as("check adding like to userId = %s", actor.getId())
                    .isEqualTo(NumberUtils.INTEGER_ONE);

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Add item to current users likes list with private owner",
            groups = {"likes"},
            expectedExceptions = Exception.class
    )
    void addLikeToPostWithPrivateOwnerErrorTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .add(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(2)
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("check exception name")
                    .isInstanceOf(ApiPrivateProfileException.class);
            assertThat(((ApiPrivateProfileException) actualException).getCode()).as("check code exception")
                    .isEqualTo(30);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Delete item from the current users likes list",
            groups = {"smoke", "likes"},
            priority = 3
    )
    void deleteLikeFromThePostTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            DeleteResponse actualResponse = vkApi.likes()
                    .delete(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(actor.getId())
                    .execute();

            assertThat(actualResponse.getLikes())
                    .as("check deleting like from userId = %s", actor.getId())
                    .isEqualTo(NumberUtils.INTEGER_ZERO);
        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Delete nonexistent item from the current users likes list",
            groups = {"likes"},
            expectedExceptions = Exception.class
    )
    void deleteLikeFromThePostErrorTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .delete(actor, Type.POST, 666)
                    .ownerId(actor.getId())
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("check exception name")
                    .isInstanceOf(ApiAccessException.class);
            assertThat(((ApiAccessException) actualException).getCode()).as("check code exception")
                    .isEqualTo(15);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Get likes owners of the item",
            groups = {"smoke", "likes"},
            priority = 2
    )
    void getLikesOwnersTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            GetListResponse actualResponse = vkApi.likes()
                    .getList(actor, Type.POST)
                    .itemId(DEFAULT_ITEM_ID)
                    .execute();

            assertThat(actualResponse.getCount()).as("check likes count")
                    .isEqualTo(NumberUtils.INTEGER_ONE);
            assertThat(actualResponse.getItems()).as("check likes owners")
                    .isEqualTo(Collections.singletonList(actor.getId()));

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    // bug of specification !!! The field item_id isn't marked as a required !!!
    @Test(
            description = "Get likes owners with empty itemId",
            groups = {"likes"},
            expectedExceptions = Exception.class
    )
    void getLikesOwnersWithoutItemIdErrorTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .getList(actor, Type.POST)
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("check exception name")
                    .isInstanceOf(ApiParamException.class);
            assertThat(((ApiParamException) actualException).getCode()).as("check code exception")
                    .isEqualTo(100);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Is current user likes this itemId",
            groups = {"smoke", "likes"},
            dataProvider = "isLikedThePost",
            dataProviderClass = LikesDataProvider.class,
            priority = 1
    )
    void isLikedThePostTest(int itemId, BoolInt boolInt) {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            IsLikedResponse actualResponse = vkApi.likes()
                    .isLiked(actor, Type.POST, itemId)
                    .execute();


            assertThat(actualResponse.getLiked()).as("check like on the item")
                    .isEqualTo(boolInt);
            assertThat(actualResponse.getCopied()).as("check copies of the item")
                    .isEqualTo(BoolInt.NO);

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Is private owner likes this itemId",
            groups = {"likes"},
            expectedExceptions = Exception.class
    )
    void isLikedThePostErrorTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .isLiked(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(1)
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("check exception name")
                    .isInstanceOf(ApiAccessException.class);
            assertThat(((ApiAccessException) actualException).getCode()).as("check code exception")
                    .isEqualTo(15);
            throw new RuntimeException(actualException);
        }
    }

}
