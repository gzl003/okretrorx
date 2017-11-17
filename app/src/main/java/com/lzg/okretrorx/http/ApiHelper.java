package com.lzg.okretrorx.http;

import com.lzg.okretrorx.application.IApplication;

/**
 *  * Created by 智光 on 2017/11/17 12:03
 *  
 */

public class ApiHelper {

    private static ApiHelper apiHelper;

    public ApiHelper getInstance() {
        if(apiHelper == null){
            apiHelper = new ApiHelper();
        }
        return apiHelper;
    }


    /**
     * 返回Api
     */
    public static IApi api() {
        return IApplication.getInstance().initIApi();
    }

}
