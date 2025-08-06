package com.hirohiro716.scent.gui;

import java.awt.event.KeyEvent;

import com.hirohiro716.scent.IdentifiableEnum;

/**
 * キーコードの列挙型。
 */
public enum KeyCode implements IdentifiableEnum<Integer> {
    /**
     * 不明なキー。
     */
    UNDEFINED(KeyEvent.VK_UNDEFINED),
    /**
     * Aキー。
     */
    A(KeyEvent.VK_A),
    /**
     * Bキー。
     */
    B(KeyEvent.VK_B),
    /**
     * Cキー。
     */
    C(KeyEvent.VK_C),
    /**
     * Dキー。
     */
    D(KeyEvent.VK_D),
    /**
     * Eキー。
     */
    E(KeyEvent.VK_E),
    /**
     * Fキー。
     */
    F(KeyEvent.VK_F),
    /**
     * Gキー。
     */
    G(KeyEvent.VK_G),
    /**
     * Hキー。
     */
    H(KeyEvent.VK_H),
    /**
     * Iキー。
     */
    I(KeyEvent.VK_I),
    /**
     * Jキー。
     */
    J(KeyEvent.VK_J),
    /**
     * Kキー。
     */
    K(KeyEvent.VK_K),
    /**
     * Lキー。
     */
    L(KeyEvent.VK_L),
    /**
     * Mキー。
     */
    M(KeyEvent.VK_M),
    /**
     * Nキー。
     */
    N(KeyEvent.VK_N),
    /**
     * Oキー。
     */
    O(KeyEvent.VK_O),
    /**
     * Pキー。
     */
    P(KeyEvent.VK_P),
    /**
     * Qキー。
     */
    Q(KeyEvent.VK_Q),
    /**
     * Rキー。
     */
    R(KeyEvent.VK_R),
    /**
     * Sキー。
     */
    S(KeyEvent.VK_S),
    /**
     * Tキー。
     */
    T(KeyEvent.VK_T),
    /**
     * Uキー。
     */
    U(KeyEvent.VK_U),
    /**
     * Vキー。
     */
    V(KeyEvent.VK_V),
    /**
     * Wキー。
     */
    W(KeyEvent.VK_W),
    /**
     * Xキー。
     */
    X(KeyEvent.VK_X),
    /**
     * Yキー。
     */
    Y(KeyEvent.VK_Y),
    /**
     * Zキー。
     */
    Z(KeyEvent.VK_Z),
    /**
     * 1キー。
     */
    DIGIT1(KeyEvent.VK_1),
    /**
     * 2キー。
     */
    DIGIT2(KeyEvent.VK_2),
    /**
     * 3キー。
     */
    DIGIT3(KeyEvent.VK_3),
    /**
     * 4キー。
     */
    DIGIT4(KeyEvent.VK_4),
    /**
     * 5キー。
     */
    DIGIT5(KeyEvent.VK_5),
    /**
     * 6キー。
     */
    DIGIT6(KeyEvent.VK_6),
    /**
     * 7キー。
     */
    DIGIT7(KeyEvent.VK_7),
    /**
     * 8キー。
     */
    DIGIT8(KeyEvent.VK_8),
    /**
     * 9キー。
     */
    DIGIT9(KeyEvent.VK_9),
    /**
     * 0キー。
     */
    DIGIT0(KeyEvent.VK_0),
    /**
     * 数字入力用キーパッド1キー。
     */
    NUMPAD1(KeyEvent.VK_NUMPAD1),
    /**
     * 数字入力用キーパッド2キー。
     */
    NUMPAD2(KeyEvent.VK_NUMPAD2),
    /**
     * 数字入力用キーパッド3キー。
     */
    NUMPAD3(KeyEvent.VK_NUMPAD3),
    /**
     * 数字入力用キーパッド4キー。
     */
    NUMPAD4(KeyEvent.VK_NUMPAD4),
    /**
     * 数字入力用キーパッド5キー。
     */
    NUMPAD5(KeyEvent.VK_NUMPAD5),
    /**
     * 数字入力用キーパッド6キー。
     */
    NUMPAD6(KeyEvent.VK_NUMPAD6),
    /**
     * 数字入力用キーパッド7キー。
     */
    NUMPAD7(KeyEvent.VK_NUMPAD7),
    /**
     * 数字入力用キーパッド8キー。
     */
    NUMPAD8(KeyEvent.VK_NUMPAD8),
    /**
     * 数字入力用キーパッド9キー。
     */
    NUMPAD9(KeyEvent.VK_NUMPAD9),
    /**
     * 数字入力用キーパッド0キー。
     */
    NUMPAD0(KeyEvent.VK_NUMPAD0),
    /**
     * 「.」キー。
     */
    PERIOD(KeyEvent.VK_PERIOD),
    /**
     * 「,」キー。
     */
    COMMA(KeyEvent.VK_COMMA),
    /**
     * 「:」キー。
     */
    COLON(KeyEvent.VK_COLON),
    /**
     * 「;」キー。
     */
    SEMICOLON(KeyEvent.VK_SEMICOLON),
    /**
     * 「/」キー。
     */
    SLASH(KeyEvent.VK_SLASH),
    /**
     * 「@」キー。
     */
    AT(KeyEvent.VK_AT),
    /**
     * 「+」キー。
     */
    PLUS(KeyEvent.VK_PLUS),
    /**
     * 「-」キー。
     */
    MINUS(KeyEvent.VK_MINUS),
    /**
     * SPACEキー。
     */
    SPACE(KeyEvent.VK_SPACE),
    /**
     * ENTERキー。
     */
    ENTER(KeyEvent.VK_ENTER),
    /**
     * ESCAPEキー。
     */
    ESCAPE(KeyEvent.VK_ESCAPE),
    /**
     * DELETEキー。
     */
    DELETE(KeyEvent.VK_DELETE),
    /**
     * BACKSPACEキー。
     */
    BACKSPACE(KeyEvent.VK_BACK_SPACE),
    /**
     * SHIFTキー。
     */
    SHIFT(KeyEvent.VK_SHIFT),
    /**
     * CONTROLキー。
     */
    CONTROL(KeyEvent.VK_CONTROL),
    /**
     * ALTキー。
     */
    ALT(KeyEvent.VK_ALT),
    /**
     * TABキー。
     */
    TAB(KeyEvent.VK_TAB),
    /**
     * 上矢印キー。
     */
    UP(KeyEvent.VK_UP),
    /**
     * 下矢印キー。
     */
    DOWN(KeyEvent.VK_DOWN),
    /**
     * 左矢印キー。
     */
    LEFT(KeyEvent.VK_LEFT),
    /**
     * 右矢印キー。
     */
    RIGHT(KeyEvent.VK_RIGHT),
    /**
     * HOMEキー。
     */
    HOME(KeyEvent.VK_HOME),
    /**
     * ENDキー。
     */
    END(KeyEvent.VK_END),
    /**
     * PgUpキー。
     */
    PAGE_UP(KeyEvent.VK_PAGE_UP),
    /**
     * PgDnキー。
     */
    PAGE_DOWN(KeyEvent.VK_PAGE_DOWN),
    /**
     * INSERTキー。
     */
    INSERT(KeyEvent.VK_INSERT),
    /**
     * ひらがなキー。
     */
    HIRAGANA(KeyEvent.VK_HIRAGANA),
    /**
     * カタカナキー。
     */
    KATAKANA(KeyEvent.VK_KATAKANA),
    /**
     * インプットメソッドのON/OFFキー。
     */
    INPUT_METHOD_ON_OFF(KeyEvent.VK_INPUT_METHOD_ON_OFF),
    /**
     * F1キー。
     */
    F1(KeyEvent.VK_F1),
    /**
     * F2キー。
     */
    F2(KeyEvent.VK_F2),
    /**
     * F3キー。
     */
    F3(KeyEvent.VK_F3),
    /**
     * F4キー。
     */
    F4(KeyEvent.VK_F4),
    /**
     * F5キー。
     */
    F5(KeyEvent.VK_F5),
    /**
     * F6キー。
     */
    F6(KeyEvent.VK_F6),
    /**
     * F7キー。
     */
    F7(KeyEvent.VK_F7),
    /**
     * F8キー。
     */
    F8(KeyEvent.VK_F8),
    /**
     * F9キー。
     */
    F9(KeyEvent.VK_F9),
    /**
     * F10キー。
     */
    F10(KeyEvent.VK_F10),
    /**
     * F11キー。
     */
    F11(KeyEvent.VK_F11),
    /**
     * F12キー。
     */
    F12(KeyEvent.VK_F12),
    ;
    
    /**
     * コンストラクタ。
     * 
     * @param keyCodeAWT
     */
    private KeyCode(int keyCodeAWT) {
        this.keyCodeAWT = keyCodeAWT;
    }
    
    private int keyCodeAWT;
    
    /**
     * このキーコードに相対するjava.awt.event.KeyEventで使用できる数値を取得する。
     * 
     * @return
     */
    public int getKeyCodeAWT() {
        return this.keyCodeAWT;
    }
    
    @Override
    public Integer getID() {
        return this.keyCodeAWT;
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
}
