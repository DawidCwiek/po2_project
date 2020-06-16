public class Status {
    private Integer online = 0;
    private Integer synchronization = 0;
    private Integer sendFile = 0;
    private Integer downloadFile = 0;
    public String status = "conecting";

    public void startStatus(String action) {
        switch(action) {
            case "online":
                this.online += 1;
                break;
            case "synchronization":
                this.synchronization += 1;
                break;
            case "sendFile":
                this.sendFile += 1;
                break;
            case "downloadFile":
                this.downloadFile += 1;
                break;
        }
        setStatus();
    }

    public void endStatus(String action) {
        switch(action) {
            case "online":
                this.online -= 1;
                break;
            case "synchronization":
                this.synchronization -= 1;
                break;
            case "sendFile":
                this.sendFile -= 1;
                break;
            case "downloadFile":
                this.downloadFile -= 1;
                break;
        }
        setStatus();
    }

    private void setStatus() {
        if(this.online == 1) {
            this.status = "online";
        }
        if(this.synchronization > 0) {
            this.status = "synchronization";
        }
        if(this.sendFile > 0) {
            this.status = "send file";
        }
        if(this.downloadFile > 0) {
            this.status = "download file";
        }
        if(this.downloadFile > 0 && this.sendFile > 0) {
            this.status = "download and send file";
        }
    }
}
