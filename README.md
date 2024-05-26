# Storage Drive

Members: Mehmet Emin Er - Mustafa Kepenek - Emir Asal


# Project Overview 

This project is about a cloud architecture for a storage system similar to Dropbox, deployed on Google Cloud Platform (GCP). The project provides users with the ability to log in & register. Upload, and manage their files securely over the internet.

### Key Features
- User Login: Allows users to create accounts, log in, and manage their profiles.
* File Upload: Users can upload files to the cloud storage and download them as needed.
* Secure Storage: Ensures that all user data is stored securely and is accessible only by authorized users.
* High Availability: The system is designed to be available and responsive at all times, even under heavy load. (Achived by auto scailing)

  # Cloud Architecture

  ## Components

  ### 1) Virtual Machines (VMs)
  - Web Server VM: Hosts the application code that manages user interactions, file uploads, and downloads.
  * Database Server VM: Stores user data, such as account information, files, and metadata.
  ### 1) Load Balancer
  + Distributes incoming traffic across multiple web server instances to ensure high availability and reliability.

## Detailed Architecture Description
2 Identical Web Server VM
* Purpose: Hosts the web application responsible for handling user interactions, file uploads, downloads, and other functionalities. 
* Configuration:
     - Deployed on a Google Compute Engine instance.
     - Scaled based on incoming traffic using instance groups.
     - Connected to the load balancer to distribute traffic.

Database Server VM
+ Purpose: Hosts the database that stores all the user data, such as account information, files, and metadata.
* Configuration:
     - Deployed on a Google Compute Engine instance with appropriate storage and performance characteristics.
     - Configured for high availability and backup using mySQL.

Load Balancer
+ Purpose: Ensures that incoming HTTP/HTTPS requests are distributed evenly across multiple web server VMs to improve availability and fault tolerance.
* Implementation:
     - Google Cloud Load Balancing: Utilized to distribute traffic across instances.
     - Backend Services: Configured to define how the load balancer routes traffic to the web server instances.

### Implementation Steps
1) Creating Web Server VM Instance
2) Creating Database Server VM Instance
3) Setting up instance Group for Load Balancer
4) Creating load balancer
5) Configuring the load balancer

## Arcitacture Diagram
![b](https://github.com/emirasal/CS-436-Project/assets/127860430/1827305c-6a07-4536-91a0-94eb474f10bd)


# Stress & Performance Testing
For Stress and Performance Testing "Locust" have been used. Test function logins, uploads a file and downloads another. The example test function is uploaded.
![a](https://github.com/emirasal/CS-436-Project/assets/127860430/7c9eec35-04e0-4c43-b820-6d99db608b89)

On GCP, E2 machine used in the first tests. However, since it was not powerful enough to cover desired traffic, application moved to N2 machines. According to stress and performance tests, application works fine with high traffic. 

* The test has been done with 100 users
* 0 failures
* 16.2 avarage RPS
* Avarage Response Per Milisecond started over 350 ms and decreased over time
    * Avarage response time started high since 100 users tried to login at the same time but it kept decreasing over the test

# Cloud Functions
To check if the note desctription is longer than 255 chars:

const functions = require('@google-cloud/functions-framework');

functions.http('helloHttp', (req, res) => {
  const requestBody = req.body;
  console.log('Request Body:', requestBody);
  
  const noteContent = requestBody.noteContent;
  console.log('Note Content:', noteContent);
  if (!noteContent) {
    res.status(400).send({ error: "No noteContent provided" });
    return;
  }

  if (noteContent.length > 255) {
    res.status(400).send({ error: "noteContent length exceeds 255 characters" });
    return;
  }

  res.status(200).send({ message: "OK" });
});



# Summary
This architecture discovers Google Cloud Platform’s capabilities to create a scalable, reliable, and high-performing storage system. The key components include web server VMs, a database server VM, and a load balancer to distribute traffic and ensure high availability. Proper configuration and setup of these components ensure the system can handle user demands effectively and maintain service continuity.
