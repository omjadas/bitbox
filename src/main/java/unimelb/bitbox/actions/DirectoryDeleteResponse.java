package unimelb.bitbox.actions;

public class DirectoryDeleteResponse implements Action {

    private static final String command = "DIRECTORY_DELETE_RESPONSE";
    private String pathName;

    public DirectoryDeleteResponse(String pathName) {
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