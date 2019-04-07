package unimelb.bitbox.actions;

public class FileDeleteResponse implements Action {

    private static final String command = "FILE_DELETE_RESPONSE";
    private String pathName;

    public FileDeleteResponse(String pathName) {
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