package com.isga.backend_helpdesk.common.exception;

/** Exception métier traduite en réponse HTTP 409 lorsqu'une donnée existe déjà. */
public class DuplicateResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }
}
