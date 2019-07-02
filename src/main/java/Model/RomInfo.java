package Model;

public class RomInfo {

    private final String serial;
    private final String region;
    private final String languages;
    private final String name;

    public RomInfo(String se, String re, String lan, String n){
        this.serial = se;
        this.region = re;
        this.languages = lan;
        this.name = n;
    }


//    public void setTitleId(String titleId) {
//        this.titleId = titleId;
//    }
//    public String getTitleId() {
//        return this.titleId;
//    }



//    public void setSerial(String titleId) {
//        this.serial = titleId;
//    }

    public String getSerial() {
        return this.serial;
    }



//    public void setRegion(String titleId) {
//        this.region = titleId;
//    }

    public String getRegion() {
        return this.region;
    }



//    public void setLanguages(String languages) {
//        this.languages = languages;
//    }

    public String getLanguages() {
        return this.languages;
    }

//    public void setName(String titleId) {
//        this.name = titleId;
//    }

    public String getName() {
        return this.name;
    }
}
