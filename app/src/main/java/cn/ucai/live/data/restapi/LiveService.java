package cn.ucai.live.data.restapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/7 0007.
 */

public interface LiveService {
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getAllGifts
    @GET("live/getAllGifts")
    Call<String> getAllGifts();
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getRechargeStatements?uname=s&pageId=1&pageSize=1
    @GET("live/getRechargeStatements")Call<String> getRechargeStatements(
            @Query("uname") String uname,
            @Query("pageId")String pageId,
            @Query("pageSize")String pageSize
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getBalance?uname=%E6%98%AF
    @GET("live/getBalance")Call<String> getBalance(@Query("uname")String uname);
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getGiftStatementsByAnchor?anchor=s
    @GET("live/getGiftStatementsByAnchor")Call<String> getGiftStatementsByAnchor(@Query("anchor")String anchor);
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/givingGifts?uname=1&anchor=1&giftId=1&giftNum=1
    @GET("live/givingGifts")Call<String> givingGifts(
            @Query("uname")String uname,
            @Query("anchor")String anchor,
            @Query("giftId")int giftId,
            @Query("giftNum")int giftNum
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getGivingGiftStatements?uname=1&pageId=1&pageSize=1
    @GET("live/getGivingGiftStatements")Call<String> getGivingGiftStatements(
            @Query("uname")String uname,
            @Query("pageId")String pageId,
            @Query("pageSize")String pageSize
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getReceivingGiftStatementsServlet?anchor=1&pageId=1&pageSize=1
    @GET("live/getReceivingGiftStatementsServlet")Call<String> getReceivingGiftStatementsServlet(
            @Query("anchor")String anchor,
            @Query("pageId")String pageId,
            @Query("pageSize")String pageSize
            );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/recharge?uname=1&rmb=1
    @GET("live/recharge")Call<String> recharge(
            @Query("uname")String uname,
            @Query("rmb")String rmb
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/createChatRoom?auth=1&name=1&description=1&owner=1&maxusers=300&members=1
    @GET("live/createChatRoom")Call<String> createChatRoom(
            @Query("auth")String auth,
            @Query("name")String name,
            @Query("description")String description,
            @Query("owner")String owner,
            @Query("maxusers")String maxusers,
            @Query("members")String members
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/uploadChatRoomAvatar?chatRoomId=1
    @POST("live/uploadChatRoomAvatar")Call<String> uploadChatRoomAvatar(
            @Query("chatRoomId")String chatRoomId
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getAllChatRoom
    @GET("live/getAllChatRoom")Call<String> getAllChatRoom();
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getChatRoomDetail?chatRoomId=1
    @GET("live/getChatRoomDetail")Call<String> getChatRoomDetail(
            @Query("chatRoomId")String chatRoomId
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/deleteChatRoom?auth=1&chatRoomId=1
    @GET("live/deleteChatRoom")Call<String> deleteChatRoom(
            @Query("auth")String auth,
            @Query("chatRoomId")String chatRoomId
    );
    //http://101.251.196.90:8080/SuperWeChatServerV2.0/live/deleteChatRoomMember?auth=1&chatRoomId=1&username=1
    @GET("live/deleteChatRoomMember")Call<String> deleteChatRoomMember(
            @Query("auth")String auth,
            @Query("chatRoomId")String chatRoomId,
            @Query("username")String username
    );
}
