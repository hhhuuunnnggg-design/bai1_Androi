package com.example.myapplication.utils;

import android.util.Patterns;

/**
 * EmailValidator
 * - Utility class để validate định dạng email
 * - Validation chặt chẽ theo chuẩn email thực tế
 */
public class EmailValidator {

    /**
     * Kiểm tra định dạng email có hợp lệ không
     *
     * @param email chuỗi email cần kiểm tra
     * @return true nếu email hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValid(String email) {
        if (email == null || email.isEmpty() || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();

        // Kiểm tra cơ bản: phải có đúng 1 dấu @
        int atCount = email.length() - email.replace("@", "").length();
        if (atCount != 1) {
            return false;
        }

        int atIndex = email.indexOf('@');

        // Phần trước @ (local part) không được rỗng
        if (atIndex <= 0) {
            return false;
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // Kiểm tra local part (phần trước @)
        if (localPart.isEmpty() || localPart.length() > 64) {
            return false;
        }

        // Local part không được bắt đầu hoặc kết thúc bằng dấu chấm
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // Local part không được có 2 dấu chấm liên tiếp
        if (localPart.contains("..")) {
            return false;
        }

        // Kiểm tra domain part (phần sau @)
        if (domainPart.isEmpty() || domainPart.length() > 255) {
            return false;
        }

        // Domain phải có ít nhất một dấu chấm
        if (!domainPart.contains(".")) {
            return false;
        }

        // Domain không được bắt đầu hoặc kết thúc bằng dấu chấm hoặc dấu gạch ngang
        if (domainPart.startsWith(".") || domainPart.endsWith(".") ||
                domainPart.startsWith("-") || domainPart.endsWith("-")) {
            return false;
        }

        // Domain không được có 2 dấu chấm liên tiếp
        if (domainPart.contains("..")) {
            return false;
        }

        // Tách domain thành các phần
        String[] domainParts = domainPart.split("\\.");
        if (domainParts.length < 2) {
            return false;
        }

        // Kiểm tra từng phần của domain
        for (String part : domainParts) {
            if (part.isEmpty() || part.length() > 63) {
                return false;
            }
            // Mỗi phần chỉ được chứa chữ, số, dấu gạch ngang
            if (!part.matches("^[a-zA-Z0-9-]+$")) {
                return false;
            }
            // Không được bắt đầu hoặc kết thúc bằng dấu gạch ngang
            if (part.startsWith("-") || part.endsWith("-")) {
                return false;
            }
        }

        // Extension (phần cuối cùng) phải là chữ cái thuần túy, ít nhất 2 ký tự
        String extension = domainParts[domainParts.length - 1];
        if (extension.length() < 2 || !extension.matches("^[a-zA-Z]+$")) {
            return false;
        }

        // Kiểm tra bằng Patterns.EMAIL_ADDRESS của Android để đảm bảo
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
