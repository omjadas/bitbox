package unimelb.bitbox.actions;

public class FileModifyRequest implements Action {

    private String pathName;

    public FileModifyRequest(String pathName) {
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