package hg.util.result;

public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200),
    /**
     * 失败
     */
    FAIL(400),

    /**
     * 未认证（签名错误）
     */
    UNAUTHORIZED(401),

    /**
     * 接口不存在
     */
    NOT_FOUND(404),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500),
    /**
     * 没有权限
     */
    NO_AUTHORITY(402),
    /**
     * 数据重复
     */
    DATA_REPEAT(501);

    public int code;

    ResultCode(int code) {
        this.code = code;
    }

}
