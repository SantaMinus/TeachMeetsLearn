package com.sava.teachernet.exception;

public class InvalidAuthException extends RuntimeException {

  public InvalidAuthException() {
    super();
  }

  public InvalidAuthException(String message) {
    super(message);
  }
}
