import java.nio.file.*;

public class Watcher extends Thread {
    private WatchService watchService;
    private Path path;
    private WatchKey key;
    private Client parent;
    private boolean pause = false;

    public Watcher(String path, Client parent) {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.path = Paths.get(path);
            this.path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            this.parent = parent;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause() {
        this.pause = true;
    }

    public void go() {
        this.pause = false;
    }

    @Override
    public void run() {
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if(!this.pause) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            this.parent.parent.statusClass.startStatus("sendFile");
                            this.parent.parent.updateStatus();
                            this.parent.comunication.sendFileAction(event.context().toString());
                            this.parent.parent.updateFileList();
                            this.parent.parent.statusClass.endStatus("sendFile");
                            this.parent.parent.updateStatus();
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            this.parent.comunication.deleteFileAction(event.context().toString());
                            this.parent.parent.updateFileList();
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            this.parent.parent.statusClass.startStatus("sendFile");
                            this.parent.parent.updateStatus();
                            this.parent.comunication.modificationFileAction(event.context().toString());
                            this.parent.parent.updateFileList();
                            this.parent.parent.statusClass.endStatus("sendFile");
                            this.parent.parent.updateStatus();
                        }
                    }
                }
                key.reset();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
