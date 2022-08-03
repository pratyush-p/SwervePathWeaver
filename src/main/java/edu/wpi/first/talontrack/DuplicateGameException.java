package edu.wpi.first.talontrack;

public class DuplicateGameException extends RuntimeException {
  public DuplicateGameException(String message) {
    super(message);
  }

  public DuplicateGameException(String message, Throwable cause) {
    super(message, cause);
  }
}
