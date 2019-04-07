package unimelb.bitbox.actions;

public class DirectoryCreateResponse implements Action {

    private static final String command = "DIRECTORY_CREATE_RESPONSE";
    private String pathName;

    public DirectoryCreateResponse(String pathName) {
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