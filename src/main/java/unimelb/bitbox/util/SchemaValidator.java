package unimelb.bitbox.util;

import java.util.ArrayList;

public class SchemaValidator {
    public static Boolean validateSchema(Document document) {
        try {
            String command = document.getString("command");
            if (command == null) {
                return false;
            } else {
                switch (command) {
                case "CONNECTION_REFUSED":
                    return validateConnectionRefused(document);
                case "HANDSHAKE_REQUEST":
                    return validateHandshakeRequest(document);
                case "HANDSHAKE_RESPONSE":
                    return validateHandshakeResponse(document);
                case "FILE_CREATE_REQUEST":
                    return validateFileCreateRequest(document);
                case "FILE_CREATE_RESPONSE":
                    return validateFileCreateResponse(document);
                case "FILE_BYTES_REQUEST":
                    return validateFileBytesRequest(document);
                case "FILE_BYTES_RESPONSE":
                    return validateFileBytesResponse(document);
                case "FILE_MODIFY_REQUEST":
                    return validateFileModifyRequest(document);
                case "FILE_MODIFY_RESPONSE":
                    return validateFileModifyResponse(document);
                case "FILE_DELETE_REQUEST":
                    return validateFileDeleteRequest(document);
                case "FILE_DELETE_RESPONSE":
                    return validateFileDeleteResponse(document);
                case "DIRECTORY_CREATE_REQUEST":
                    return validateDirectoryCreateRequest(document);
                case "DIRECTORY_CREATE_RESPONSE":
                    return validateDirectoryCreateResponse(document);
                case "DIRECTORY_DELETE_REQUEST":
                    return validateDirectoryDeleteRequest(document);
                case "DIRECTORY_DELETE_RESPONSE":
                    return validateDirectoryDeleteResponse(document);
                default:
                    return false;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static boolean validateConnectionRefused(Document document) {
        boolean peersValidated = validatePeers(document);
        boolean messageValidated = validateString(document, "message");

        return peersValidated && messageValidated;
    }

    private static boolean validateHandshakeRequest(Document document) {
        return validateHostPort((Document) document.get("hostPort"));
    }

    private static boolean validateHandshakeResponse(Document document) {
        return validateHostPort((Document) document.get("hostPort"));
    }

    private static boolean validateFileCreateRequest(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");

        return fileDescriptorValidated && pathNameValidated;
    }

    private static boolean validateFileCreateResponse(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return fileDescriptorValidated && pathNameValidated && messageValidated && statusValidated;
    }

    private static boolean validateFileBytesRequest(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");
        boolean positionValidated = validateInt(document, "position");
        boolean lengthValidated = validateInt(document, "length");

        return fileDescriptorValidated && pathNameValidated && positionValidated && lengthValidated;
    }

    private static boolean validateFileBytesResponse(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");
        boolean positionValidated = validateInt(document, "position");
        boolean lengthValidated = validateInt(document, "length");
        boolean contentValidated = validateString(document, "content");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return fileDescriptorValidated && pathNameValidated && positionValidated && lengthValidated && contentValidated
                && messageValidated && statusValidated;
    }

    private static boolean validateFileDeleteRequest(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");

        return fileDescriptorValidated && pathNameValidated;
    }

    private static boolean validateFileDeleteResponse(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return fileDescriptorValidated && pathNameValidated && messageValidated && statusValidated;
    }

    private static boolean validateFileModifyRequest(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");

        return fileDescriptorValidated && pathNameValidated;
    }

    private static boolean validateFileModifyResponse(Document document) {
        boolean fileDescriptorValidated = validateFileDescriptor((Document) document.get("fileDescriptor"));
        boolean pathNameValidated = validateString(document, "pathName");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return fileDescriptorValidated && pathNameValidated && messageValidated && statusValidated;
    }

    private static boolean validateDirectoryCreateRequest(Document document) {
        return validateString(document, "pathName");
    }

    private static boolean validateDirectoryCreateResponse(Document document) {
        boolean pathNameValidated = validateString(document, "pathName");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return pathNameValidated && messageValidated && statusValidated;
    }

    private static boolean validateDirectoryDeleteRequest(Document document) {
        return validateString(document, "pathName");
    }

    private static boolean validateDirectoryDeleteResponse(Document document) {
        boolean pathNameValidated = validateString(document, "pathName");
        boolean messageValidated = validateString(document, "message");
        boolean statusValidated = validateBoolean(document, "status");

        return pathNameValidated && messageValidated && statusValidated;
    }

    private static boolean validateHostPort(Document document) {
        boolean hostValidated = validateString(document, "host");
        boolean portValidated = validateInt(document, "port");

        return hostValidated && portValidated;
    }

    private static boolean validateFileDescriptor(Document document) {
        boolean md5Validated = validateString(document, "md5");
        boolean lastModifiedValidated = validateInt(document, "lastModified");
        boolean fileSizeValidated = validateInt(document, "fileSize");

        return md5Validated && lastModifiedValidated && fileSizeValidated;
    }

    private static boolean validatePeers(Document document) {
        ArrayList<Document> hostPorts = (ArrayList<Document>) document.get("peers");

        if (hostPorts == null) {
            return false;
        }

        for (Document hostPort : hostPorts) {
            if (!validateHostPort(hostPort)) {
                return false;
            }
        }

        return true;
    }

    private static boolean validateString(Document document, String key) {
        try {
            String string = document.getString(key);
            return (string != null);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    private static boolean validateInt(Document document, String key) {
        try {
            document.getLong(key);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }

        return true;
    }

    private static boolean validateBoolean(Document document, String key) {
        try {
            document.getBoolean(key);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }

        return true;
    }
}