package bjd;

/**
 * Reg用のチェック例外<br>
 * 
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public class RegException extends Exception {
	public RegException(String msg) {
		super(msg);
	}
}