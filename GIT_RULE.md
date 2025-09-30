⸻

🚀 Git Convention – Quy ước Git cho Dự án

1. Cấu trúc Repo
	•	Repo chung gồm 2 folder chính:

/frontend
/backend


	•	Code FE để trong frontend/, code BE để trong backend/.
	•	Tài liệu chung (docs, readme) để ở thư mục gốc.

⸻

2. Branching Rule
	•	main: code ổn định, dùng để deploy.
	•	feature/<ten-chuc-nang>: phát triển tính năng.
	•	fix/<ten-loi>: sửa bug.
	•	hotfix/<ten-loi>: sửa gấp lỗi production.

👉 Mỗi task/issue → 1 branch riêng, không code trực tiếp trên main.

⸻

3. Commit Convention
	•	Format:

<type>: <short description>


	•	Các type được dùng:
	•	feat: thêm chức năng mới
	•	fix: sửa lỗi
	•	docs: cập nhật tài liệu
	•	style: chỉnh style code (không ảnh hưởng logic)
	•	refactor: cải tiến code mà không đổi chức năng
	•	test: thêm/chỉnh test
	•	chore: việc vặt (config, build, update lib)

Ví dụ:
	•	feat: add login API
	•	fix: resolve null pointer in OrderService
	•	docs: update readme with setup guide

⸻

4. Pull Request & Code Review
	•	Tất cả merge vào main phải qua Pull Request (PR).
	•	PR phải có ít nhất 1 reviewer approve mới được merge.
	•	Nội dung PR cần: mô tả chức năng/lỗi đã làm, link task nếu có.

⸻

5. Code Style
	•	Java/Backend: camelCase cho biến/hàm, PascalCase cho class.
	•	JavaScript/Frontend: camelCase cho biến/hàm, PascalCase cho component React.
	•	Dùng ESLint/Prettier (FE) và Checkstyle (BE) để format code.
	•	Không push code lỗi hoặc đang bug nặng lên repo.

⸻

6. Task Management
	•	Mỗi người nhận task trên board (Trello/Jira/GitHub Projects).
	•	Tên branch nên trùng với task:

feature/login-page
fix/cart-bug


	•	Khi hoàn thành → mở PR → assign reviewer.

⸻

7. Nguyên tắc làm việc nhóm
	•	Code phải pull mới nhất trước khi push.
	•	Luôn tạo branch mới, không commit trực tiếp vào main.
	•	Gặp conflict → tự xử lý, nếu khó thì nhờ team hỗ trợ.
	•	Commit nhỏ, rõ ràng, không dồn tất cả thay đổi vào 1 commit.

⸻

📌 Lời nhắn từ Team Lead:
Mọi người tuân thủ quy tắc này để tránh lỗi xung đột code và giúp dự án phát triển nhanh, gọn, chuyên nghiệp.

⸻