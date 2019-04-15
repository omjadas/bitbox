package unimelb.bitbox;

import unimelb.bitbox.util.Document;

public class FileDescriptor {
    /**
     * Timestamp of the last modification time of the file.
     */
    public long lastModified;
    /**
     * The MD5 hash of the file's content.
     */
    public String md5;
    /**
     * The size of the file in bytes.
     */
    public long fileSize;

    /**
     * Constructor
     * 
     * @param lastModified the timestamp for when file was last modified
     * @param md5          the current MD5 hash of the file's content.
     */
    public FileDescriptor(long lastModified, String md5, long fileSize) {
        this.lastModified = lastModified;
        this.md5 = md5;
        this.fileSize = fileSize;
    }

    /**
     * Constructor
     * 
     * @param message the document containing the fileDescriptor
     */
    public FileDescriptor(Document message) {
        this.lastModified = ((Document) message.get("fileDescriptor")).getLong("lastModified");
        this.md5 = ((Document) message.get("fileDescriptor")).getString("md5");
        this.fileSize = ((Document) message.get("fileDescriptor")).getLong("fileSize");
    }

    /**
     * Provide the {@link #Document} for this object.
     */
    public Document toDoc() {
        Document doc = new Document();
        doc.append("lastModified", lastModified);
        doc.append("md5", md5);
        doc.append("fileSize", fileSize);
        return doc;
    }
}
