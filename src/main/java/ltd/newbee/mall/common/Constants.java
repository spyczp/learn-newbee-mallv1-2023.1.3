package ltd.newbee.mall.common;

/**
 * @apiNote 常量配置
 */
public class Constants {

    public final static String FILE_UPLOAD_DIC = "D:\\upload\\";//上传文件的默认url前缀，根据部署设置自行修改

    public final static String FILE_IMG_DIC = "D:\\upload\\goods_img\\";//商城商品图片目录

    public final static int INDEX_CAROUSEL_COUNT = 5;//首页轮播图数量(可根据自身需求修改)

    public final static int INDEX_CATEGORY_NUMBER = 10;//首页一级分类的最大数量

    public final static int SEARCH_CATEGORY_NUMBER = 8;//搜索页一级分类的最大数量

    public final static int INDEX_GOODS_HOT_NUMBER = 4;//首页热卖商品数量
    public final static int INDEX_GOODS_NEW_NUMBER = 5;//首页新品数量
    public final static int INDEX_GOODS_RECOMMEND_NUMBER = 10;//首页推荐商品数量

    public final static int SHOPPING_CART_ITEM_TOTAL_NUMBER = 13;//购物车中商品的最大数量(可根据自身需求修改)

    public final static int SHOPPING_CART_ITEM_LIMIT_NUMBER = 5;//购物车中单个商品的最大购买数量(可根据自身需求修改)

    public final static String MALL_VERIFY_CODE_KEY = "mallVerifyCode";//验证码key

    public final static String MALL_USER_SESSION_KEY = "newBeeMallUser";//session中user的key

    public final static int GOODS_SEARCH_PAGE_LIMIT = 10;//搜索分页的默认条数(每页10条)

    public final static int ORDER_SEARCH_PAGE_LIMIT = 3;//我的订单列表分页的默认条数(每页3条)

    public final static int SELL_STATUS_UP = 0;//商品上架状态
    public final static int SELL_STATUS_DOWN = 1;//商品下架状态

    public final static String ERROR_MSG = "errorMsg"; //错误信息

    public final static String VERIFY_CODE = "verifyCode"; //验证码

    public final static String SESSION_LOGIN_USER = "loginUser"; //登录用户昵称

    public final static String SESSION_LOGIN_USER_ID = "loginUserId"; //登录用户id

    public final static String MALL_USER_LOGIN_ID = "mallUserLoginId"; //商城登录用户名

    public final static String MALL_USER_LOGIN_NAME = "mallUserLoginName"; //商城登录用户id

    public final static String MALL_USER_NICK_NAME = "mallUserNickName"; //商城登录用户昵称

    public final static String MALL_USER_ADDRESS = "mallUserAddress"; // 商城登录用户的地址
}
