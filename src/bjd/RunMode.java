package bjd;

/**
 * アプリケーションの動作モード<br>
 * 
 * Normal 通常起動  (ウインドあり)<br>
 * NormalRegist 通常起動（サ-ビス登録済み）(ウインドあり)<br>
 * Remote リモート（ウインドあり）<br>
 * Service サービス起動　(ウインドなし)<br>
 * 
 * @author SIN
 *
 */
public enum RunMode {
    Normal, //通常起動  (ウインドあり)
    NormalRegist, //通常起動（サ-ビス登録済み）(ウインドあり)
    Remote, //リモート（ウインドあり）
    Service, //サービス起動　(ウインドなし)
}
