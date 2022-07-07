package ru.vk.dataProvider;

import com.vk.api.sdk.objects.base.BoolInt;

import org.apache.commons.lang3.math.NumberUtils;
import org.testng.annotations.DataProvider;

import java.util.Random;

public class LikesDataProvider {

    @DataProvider
    public Object[][] isLikedThePost() {
        int randomItemId = new Random().nextInt(20) + 2;
        return new Object[][] {
                {NumberUtils.INTEGER_ONE, BoolInt.YES},
                {randomItemId, BoolInt.NO},
        };
    }

}
