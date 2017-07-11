package com.ditclear.stateflowlayout;

/**
 * 页面描述：
 *
 * Created by ditclear on 2017/7/11.
 */

public interface Node {

    boolean isChild();

    boolean isNextChild();

    String getState();

    String getContent();


}
