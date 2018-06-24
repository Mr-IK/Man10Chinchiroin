package red.man10.man10chinchirorin;

public class RoleData {
    static Man10Chinchirorin plugin;
    public static void loadEnable(Man10Chinchirorin plugin){
        MCRData.plugin = plugin;
    }

    public static String mainhantei(int i,int ii,int iii){
        if(i == 1&&i == ii&&i==iii) {
            return "ピンゾロ";
        }else if(i == ii&&i==iii) {
            return "ゾロメ";
        }else if(i+ii+iii==10){
            return "man10";
        }else if(i+ii+iii==5){
            return "dan5";
        }else if(i == ii){
            if(iii==1) {
                return "イチ";
            }else if(iii==2) {
                return "ニ";
            }else if(iii==3) {
                return "サン";
            }else if(iii==4) {
                return "シ";
            }else if(iii==5) {
                return "ゴ";
            }else if(iii==6) {
                return "ロ";
            }
        }else if(i == iii){
            if(ii==1) {
                return "イチ";
            }else if(ii==2) {
                return "ニ";
            }else if(ii==3) {
                return "サン";
            }else if(ii==4) {
                return "シ";
            }else if(ii==5) {
                return "ゴ";
            }else if(ii==6) {
                return "ロ";
            }
        }else if(ii == iii){
            if(i==1) {
                return "イチ";
            }else if(i==2) {
                return "ニ";
            }else if(i==3) {
                return "サン";
            }else if(i==4) {
                return "シ";
            }else if(i==5) {
                return "ゴ";
            }else if(i==6) {
                return "ロ";
            }
        }else{
            return "ナシ";
        }
        return "ナシ";
    }

}
