package cloud.simpledoc.domain.model.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GenerateFileException extends Exception {
    Integer code;
    String message;
    Exception exception;
}
