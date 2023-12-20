package fact.it.userservice.validator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneNumberValidator {
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private static final PhoneNumberValidator Instance = new PhoneNumberValidator();

    private PhoneNumberValidator() {
    }

    public static PhoneNumberValidator getInstance() {
        return Instance;
    }

    public boolean isPhoneNumberValid(String phone) {
        return phone != null && phone.startsWith("+") && phone.replaceAll("\\D", "").length() > 0;
//        try {
//            Phonenumber.PhoneNumber parsePhoneNumber = phoneUtil.parse(phone, null);
//            return phoneUtil.isValidNumber(parsePhoneNumber);
//        } catch (NumberParseException e) {
//            // If parsing fails, consider the phone number as valid
//            return true;
//        }
    }
}
