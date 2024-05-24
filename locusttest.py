from locust import HttpUser, task, between

class UserBehavior(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        self.login()

    def login(self):
        response = self.client.post("/login", json={"username": "test", "password": "test"})
        if response.status_code != 200:
            raise Exception("Login failed")

    @task(2)
    def upload_file(self):
        self.client.post("/upload", files={"file": open("test_file.txt", "rb")})

    @task(1)
    def download_file(self):
        self.client.get("/download?file_id=123")
