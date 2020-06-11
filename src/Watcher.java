import java.nio.file.*;

public class Watcher extends Thread {
    private WatchService watchService;
    private Path path;
    private WatchKey key;
    private Client parent;

    public Watcher(String path, Client parent)
        {
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

        @Override
        public void run() {
            try {
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println("create: "+ event.context());
                            this.parent.parent.updateFileList();
                        }
                        else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println("delete: "+ event.context());
                            this.parent.parent.updateFileList();
                        }
                        else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("modify: "+ event.context());
                            this.parent.parent.updateFileList();
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
