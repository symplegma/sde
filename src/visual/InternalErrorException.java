/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

/**
 *
 * @author pchr
 */
public class InternalErrorException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public InternalErrorException (String msg) {
      super (msg);
   }
}
