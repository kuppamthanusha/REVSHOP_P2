## **RevShop E-Commerce Microservices Project**

This project is an E-Commerce Platform built using Spring Boot and a Microservices Architecture. It includes several independent services that work together to provide a seamless shopping experience.

## **📂 Project Structure**

![image](https://github.com/user-attachments/assets/5fd58185-0b4c-4211-8087-f033c322649a)


## 🚀 Technologies Used**

**✅ Java (Spring Boot)**
Java is a widely used programming language that provides a secure and scalable environment for developing applications.

**Spring Boot** is a Java framework used to build production-ready applications quickly.  
It simplifies configuration and deployment by:  
- **Providing an embedded server** (like Tomcat).  
- **Reducing boilerplate code** with auto-configuration.  
- **Supporting dependency injection** and transaction management.  

👉 **Use in Project:**  
- **Backend logic** for the microservices.  
- Handles **request processing** and **business logic**.  
- Manages **dependencies** and **configurations**.  

---

**✅ Spring Cloud (Eureka, API Gateway)**  
**Spring Cloud** is a set of tools that helps manage microservices communication and configuration.  

**Eureka (Service Discovery):**  
- **Registers microservices** and enables them to discover each other.  
- Ensures that services know the location of other services dynamically.  

**API Gateway:**  
- Acts as a **single entry point** for client requests.  
- Redirects the request to the appropriate microservice.  

👉 **Use in Project:**  
- **Eureka** → Registers `cartservice`, `productservice`, `payment`, etc.  
- **API Gateway** → Routes all requests through a central entry point, improving scalability and security.  

---

**✅ Hibernate (JPA)**  
**Hibernate** is an Object-Relational Mapping (ORM) framework.  
- It maps **Java objects** to **database tables**.  
- Simplifies **CRUD** (Create, Read, Update, Delete) operations.  
- Automatically generates **SQL queries** based on Java objects.  
 

👉 **Use in Project:**  
- Used to handle **database interactions** for product details, user data, and order management.  
- Reduces the need for writing raw SQL queries.  

---

**✅ MySQL (Database)**  
**MySQL** is a relational database used to store structured data.  
- Uses **SQL** (Structured Query Language) for data manipulation.  

👉 **Use in Project:**  
- Stores **user data**, **product details**, **cart information**, **order history**, and **payment details**.  
- Ensures data consistency between different microservices.  

---

**✅ RESTful API**  
A **RESTful API** (Representational State Transfer) is an interface that allows communication between services over HTTP using standard methods like:  
- **GET** – Retrieve data  
- **POST** – Create new data  
- **PUT** – Update existing data  
- **DELETE** – Remove data  

👉 **Use in Project:**  
- Exposes endpoints for client requests.  
**Example:**  
- `/products` → Get product list  
- `/cart` → Add or remove items from the cart  
- `/order` → Place an order  


