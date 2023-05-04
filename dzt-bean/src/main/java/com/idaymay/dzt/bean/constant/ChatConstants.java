package com.idaymay.dzt.bean.constant;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:39
 */
public class ChatConstants {

   public static final String DEFAULT_ANSWER = "请先提问吧！";

   public static final String THINKING = "思考中，请稍后再回复提问的ID。";

   public static final String ANSWER_PRE = "QUESTION";

   public static final String QUESTION_ID_PRE = "QUESTION";

   public static final String SLOWDOWN = "脑力有限，容我缓缓~";

   public static final String QUESTION_PRE = "提问:";

   public static final String UNKNOWN_YOUR_QUESTION =
           "想跟我聊天吗，我会返回一串字符，回复： " +
           "\n\"提问的ID\",即可获取回复。";

   public static final String QUICK_ANSWER = "已经接收到信息,请耐心等待。提问ID:%s" +
           "\n回复：\"提问的ID\",即可获取回答。祝您生活愉快！";

   public static final String SET_API_KEY = "回复：\"/setApiKey $openAiApiKey\"初始化您的apiKey";

   public static final String API_KEY_ERROR = "apiKey无效。";

   public static final String ANSWER_ERROR = "暂时回答不了，抱歉。";

   public static final Integer FREE_COUNT = 5;

   public static final String OUT_OF_FREE_COUNT = "达到今天免费试用上限次数" + ChatConstants.FREE_COUNT + "，明天再聊吧！" +
           "\n\n添加自己的apiKey,回复：" +
           "\n\"/setApiKey ${您的openAiApiKey}\" 后无此限制。";

   public static final Integer QUESTION_REQUEST_TOTAL_TIMES = 2;

   public static final Long QUESTION_REQUEST_WAIT_TIMEOUT = 3000L;

   public static final Long QUESTION_LAST_REQUEST_WAIT_TIMEOUT = 1000L;

   public static final String ANSWER_CONTINUE = "继续";

   public static final String CONTINUE_SUB = "\n\n后面还有内容，请回复:\"继续\"，或者提问ID:%s,获取下页内容。";

   public static final String REPEAT_QUESTION = "对不起，你说的是不是：\"%s\"" +
           "\n\n请回答：\"是\"或\"不是\"";

   public static final String REPEAT_PRE = "对不起";

   public static final String QUESTION_CHECK_RESULT_NO = "不是";

   public static final String QUESTION_CHECK_RESULT_YES = "是";

   public static final String[] REPEAT_QUESTION_ANSWER_NO = {"行，你玩我！","就你调皮！","逗我呢！","什么？"};

}
