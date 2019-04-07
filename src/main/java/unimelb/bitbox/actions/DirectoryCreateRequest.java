package unimelb.bitbox.actions;

public class DirectoryCreateRequest implements Action {

    private static final String command = "DIRECTORY_CREATE_REQUEST";
    private String pathName;

    public DirectoryCreateRequest(String pathName) {
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