﻿# Codebook (not maintained) video - <a href="https://www.linkedin.com/posts/istiaqueahmedarik_raisul-islam-rahad-and-i-developed-a-small-activity-7137422087320739840-VeOO?utm_source=share&utm_medium=member_desktop"> link </a>

## Description
Codebook is a code sharing platform, where many aspiring programmers can share their code and learn from others, It includes
- Code sharing
- Auto verify code
- Explaination and time complexity of code using PALM 2 AI
- Add code to your favourites and download as pdf
- Chat with other users in realtime
- Add friends and follow other users
- A contribution ranklist
- A Chatbot to help you with your doubts powered by PALM 2 AI
- Natural language search for code
- Personal profile


## Table of Contents
- [Codebook](#codebook)
  - [Description](#description)
  - [How to run](#how-to-run)
  - [Thanks](#thanks)
  - [How to contribute](#how-to-contribute)

## How to run
1. Create a project on Firebase and initialize firestore, storage and authentication
2. create a file in resource folder [here](src\main\resources\com\codebook) named `service_acc.json`
3. copy the service account key from firebase and paste it in the file created in step 2
4. create account on supabase [here](https://app.supabase.io/)
5. create account on CLIST [here](https://clist.by/)
6. create account on PALM2API [here](https://developers.generativeai.google/guide)
7. create an file on the root directory of the project named `.env`
8. copy the following code in the file created in step 7
```bash
CODEBOOK_PALM_API_KEY=<your palm2api key>
CODEBOOK_FIREBASE_API_KEY=<your firebase api key>
CODEBOOK_SUPABASE_API_KEY=<your supabase api key>
CODEBOOK_SUPABASE_BASE_URL=<your supabase base url>
CODEBOOK_CLIST_API_KEY=<your clist api key>
```

10. run the following command in the root directory of the project
```bash
mvn clean install
```
11. run the following command in the root directory of the project
```bash
mvn clean javafx:run
```

### Thanks
Thanks to [raisulll](https://github.com/Raisulll/) for his amazing contribution to the project's frontend.

### How to contribute
1. Clone the repository
2. Create a new branch
3. Make your changes
4. Push your changes to the new branch
