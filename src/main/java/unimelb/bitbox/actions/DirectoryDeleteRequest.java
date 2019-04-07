package unimelb.bitbox.actions;

public class DirectoryDeleteRequest implements Action {

    private String pathName;

    public DirectoryDeleteRequest(String pathName) {
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