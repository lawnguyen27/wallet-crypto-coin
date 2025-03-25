package vn.sparkminds.template.constant;

public final class ErrorConstants {

    private ErrorConstants() {}

    public static final String ERROR_RESOURCE_NOT_FOUND = "error.resource.not-found";
    public static final String ERROR_INTERNAL_SERVER_ERROR = "error.internal-server-error";
    public static final String ERROR_INVALID_REQUEST = "error.invalid-request";

    public static final String ERROR_COIN_NAME_REQUIRED = "error.validation.coin.name.required";
    public static final String ERROR_COIN_NAME_LENGTH = "error.validation.coin.name.length";
    public static final String ERROR_COIN_LOGO_URL_REQUIRED = "error.validation.coin.logoUrl.required";
    public static final String ERROR_COIN_LOGO_URL_LENGTH = "error.validation.coin.logoUrl.length";

    public static final String ERROR_ASSET_ID_REQUIRED = "error.validation.asset.id.required";
    public static final String ERROR_VAULT_ID_REQUIRED = "error.validation.vault.id.required";
    public static final String ERROR_ADDRESS_REQUIRED = "error.validation.address.required";

    public static final String ERROR_TRANSACTION_WITHDRAW_EXISTED = "error.validation.transaction.withdraw.existed";
    public static final String ERROR_WALLET_BALANCE_INSUFFICIENT = "error.validation.wallet.balance.insufficient";


    public static final String ERROR_AMOUNT_REQUIRED = "error.validation.amount.required";

    public static final String ERROR_EMAIL_REQUIRED = "error.validation.email.required";
    public static final String ERROR_EMAIL_INVALID = "error.validation.email.invalid";

    public static final String ERROR_PASSWORD_REQUIRED = "error.validation.password.required";
}
