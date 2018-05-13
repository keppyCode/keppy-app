package com.qunyi.modules.utils;

public class ResultStatusCode {

    public static class INVALID_CLIENTID{

        private static String errcode = "30003";
        private static String errmsg = "Invalid clientid";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }

    public static class INVALID_PASSWORD{

        private static String errcode = "30004";
        private static String errmsg = "User name or password is incorrect";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }

    public static class INVALID_CAPTCHA{

        private static String errcode = "30005";
        private static String errmsg = "Invalid captcha or captcha overdue";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }

    public static class INVALID_TOKEN{

        private static String errcode = "30006";
        private static String errmsg = "Invalid token";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }

    public static class OK{

        private static String errcode = "200002";
        private static String errmsg = "success";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }

    public static class SYSTEM_ERR{

        private static String errcode = "200005";
        private static String errmsg = "Exception is system error";

        public static String getErrcode() {
            return errcode;
        }

        public static String getErrmsg() {
            return errmsg;
        }
    }




}
