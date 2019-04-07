package unimelb.bitbox.actions;

public class FileDeleteRequest implements Action {

    private static final String command = "FILE_DELETE_REQUEST";
    private String pathName;

    public FileDeleteRequest(String pathName) {
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