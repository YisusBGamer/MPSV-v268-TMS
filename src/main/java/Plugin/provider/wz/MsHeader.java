package Plugin.provider.wz;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName MsHeader.java
 * @Description 自己想的
 * @createTime 2024-12-22 22:07
 */

public class MsHeader {
    private String filePath;

    public int EntryCount;
    public long DataStartPosition;
    public String FileNameWithSalt;
    public String KeySalt;

    public MsHeader(String filePath) {
        this.filePath = filePath;
    }


}
