# Collab Eyewear – AI-Powered E-Commerce Mobile Application

An Android e-commerce application for Collab Eyewear, developed with **Android Studio, Firebase, n8n, and Google Gemini**.

## Overview

The application supports product discovery, authentication, shopping cart, checkout, order tracking, reviews, and personalized promotions.

A key feature is an **AI-powered promotion workflow** that combines customer purchase history and browsing behavior to generate personalized promotions and notifications.

## AI Promotion Workflow

<img width="574" height="238" alt="Screenshot 2026-08-16 230948" src="https://github.com/user-attachments/assets/bd4c3e52-5211-46d3-8569-73dad30fc5b4" />

The workflow uses **n8n** to retrieve customer data from Firebase and connects it with a **Gemini-powered AI Agent**. The agent combines predefined business rules with customer behavior to determine promotion eligibility, discount type, and personalized notification content. The generated results are then stored back in Firebase.

## Tech Stack

- **Mobile:** Android Studio
- **Backend & Database:** Firebase
- **AI Workflow:** n8n
- **LLM:** Google Gemini
- **Admin Web:** Angular

## Key Features

- Product search, filtering, and browsing
- Cart and checkout
- Order tracking and reviews
- Personalized promotions
- AI-generated promotion notifications
- Admin product, promotion, and order management
