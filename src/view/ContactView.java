package view;

import controller.ContactController;

import java.util.Scanner;

public class ContactView {
    private final Scanner scanner = new Scanner(System.in);
    private final ContactController controller = new ContactController();
    private final String FILE_PATH = "data/contacts.csv";

    public void run() {
        int choice;
        do {
            System.out.println("----- QUẢN LÝ DANH BẠ -----");
            System.out.println("1. Xem danh sách");
            System.out.println("2. Thêm mới");
            System.out.println("3. Cập nhật");
            System.out.println("4. Xoá");
            System.out.println("5. Tìm kiếm");
            System.out.println("6. Đọc từ file");
            System.out.println("7. Ghi vào file");
            System.out.println("8. Thoát");
            System.out.print("Chọn: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    controller.showContacts();
                    break;
                case 2:
                    controller.addContact();
                    break;
                case 3:
                    System.out.print("Nhập SĐT cần sửa: ");
                    controller.updateContact(scanner.nextLine());
                    break;
                case 4:
                    System.out.print("Nhập SĐT cần xoá: ");
                    controller.deleteContact(scanner.nextLine());
                    break;
                case 5:
                    System.out.print("Từ khoá tìm: ");
                    controller.search(scanner.nextLine());
                    break;
                case 6:
                    controller.readFromFile(FILE_PATH);
                    break;
                case 7:
                    controller.writeToFile(FILE_PATH);
                    break;
                case 8:
                    System.out.println("Tạm biệt!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (choice != 8);
    }
}