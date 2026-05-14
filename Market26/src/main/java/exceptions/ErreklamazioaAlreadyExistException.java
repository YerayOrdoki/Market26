package exceptions;
public class ErreklamazioaAlreadyExistException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public ErreklamazioaAlreadyExistException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public ErreklamazioaAlreadyExistException(String s)
  {
    super(s);
  }
}