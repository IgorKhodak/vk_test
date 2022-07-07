package ru.vk.likes;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAccessException;
import com.vk.api.sdk.exceptions.ApiParamException;
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
            description = "Add new like",
            groups = {"smoke", "likes"},
            priority = 0
    )
    void addLikeToThePostTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            AddResponse actualResponse = vkApi.likes()
                    .add(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(actor.getId())
                    .execute();

            assertThat(actualResponse.getLikes()).as("")
                    .isNotNull();
            assertThat(actualResponse.getLikes())
                    .as("check adding like to userId = %s", actor.getId())
                    .isEqualTo(NumberUtils.INTEGER_ONE);

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Add new like",
            groups = {"smoke", "likes"},
            expectedExceptions = Exception.class
//            expectedExceptionsMessageRegExp = "com.vk.api.sdk.exceptions.ApiParamException: One of the parameters specified was missing or invalid (100): One of the parameters specified was missing or invalid: object not found"
    )
    void addLikeToThePostNegativeTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

             vkApi.likes()
                    .add(actor, Type.AUDIO, DEFAULT_ITEM_ID)
                    .ownerId(actor.getId())
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("")
                            .isInstanceOf(ApiParamException.class);
            assertThat(((ApiParamException) actualException).getCode()).as("")
                    .isEqualTo(100);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Delete likes",
            groups = {"smoke", "likes"},
            priority = 3
    )
     void deleteLikeTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            DeleteResponse actualResponse = vkApi.likes()
                    .delete(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(actor.getId())
                    .execute();

            assertThat(actualResponse.getLikes())
                    .as("check deleting like to userId = %s", actor.getId())
                    .isEqualTo(NumberUtils.INTEGER_ZERO);
        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "Delete likes",
            groups = {"smoke", "likes"},
            expectedExceptions = Exception.class
    )
    void deleteLikeNegativeTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .delete(actor, Type.POST, 666)
                    .ownerId(actor.getId())
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("")
                    .isInstanceOf(ApiAccessException.class);
            assertThat(((ApiAccessException) actualException).getCode()).as("")
                    .isEqualTo(15);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "get likes list",
            groups = {"smoke", "likes"},
            priority = 2
    )
     void getLikesListTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            GetListResponse actualResponse = vkApi.likes()
                    .getList(actor, Type.POST)
                    .itemId(DEFAULT_ITEM_ID)
                    .execute();

            assertThat(actualResponse.getCount()).as("")
                    .isEqualTo(NumberUtils.INTEGER_ONE);
            assertThat(actualResponse.getItems()).as("")
                    .isEqualTo(Collections.singletonList(actor.getId()));

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "get likes list",
            groups = {"smoke", "likes"},
            expectedExceptions = Exception.class
    )
    void getLikesListNegativeTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            vkApi.likes()
                    .getList(actor, Type.POST)
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("")
                    .isInstanceOf(ApiParamException.class);
            assertThat(((ApiParamException) actualException).getCode()).as("")
                    .isEqualTo(100);
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "is likes",
            groups = {"smoke", "likes"},
            dataProvider = "isLikedWithDiffItemId",
            dataProviderClass = LikesDataProvider.class,
            priority = 1
    )
     void isLikeTest(int itemId, BoolInt boolInt) {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

            IsLikedResponse actualResponse = vkApi.likes()
                    .isLiked(actor, Type.POST, itemId)
                    .execute();

//            assertThat(actualResponse.isLiked()).as("")
//                            .isTrue();
            assertThat(actualResponse.getLiked()).as("")
                            .isEqualTo(boolInt);
            assertThat(actualResponse.isCopied()).as("")
                            .isFalse();
            assertThat(actualResponse.getCopied()).as("")
                            .isEqualTo(BoolInt.NO);

        } catch (Exception actualException){
            throw new RuntimeException(actualException);
        }
    }

    @Test(
            description = "is likes",
            groups = {"smoke", "likes"},
            expectedExceptions = Exception.class
    )
    void isLikeNegativeTest() {
        try {
            UserActor actor = UserProvider.getUserActor(authResponse);

           vkApi.likes()
                    .isLiked(actor, Type.POST, DEFAULT_ITEM_ID)
                    .ownerId(1)
                    .execute();

        } catch (Exception actualException) {
            assertThat(actualException).as("")
                    .isInstanceOf(ApiAccessException.class);
            assertThat(((ApiAccessException) actualException).getCode()).as("")
                    .isEqualTo(15);
            throw new RuntimeException(actualException);
        }
    }

}
