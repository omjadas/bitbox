package unimelb.bitbox.actions;

public class FileBytesRequest implements Action {

    private static final String command = "FILE_BYTES_REQUEST";
    private String pathName;

    public FileBytesRequest(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

}