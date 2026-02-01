📘 PROJECT OVERVIEW — A.R.O.U.R.A
Project Name

A.R.O.U.R.A
AI for Relief of Overthinking, Uncertainty, Restlessness & Anxiety
Tagline: Built to quiet the noise inside your head.

1️⃣ What is A.R.O.U.R.A?

A.R.O.U.R.A is an India-first mental health support platform consisting of:

A Web App (first release)

An Android App (later)

The platform focuses on non-clinical, non-diagnostic mental health support, offering:

Anxiety relief

Depression & loneliness support

Panic and crisis assistance

Emotional reflection

Spiritual/devotional comfort (multi-religion)

This is NOT a medical diagnosis or treatment app.
It is a support, comfort, and safety platform.

2️⃣ Core Philosophy (VERY IMPORTANT)

Calm > Features

Safety > Intelligence

Simplicity > Overengineering

Ethics > Growth

India-first > Global assumptions

The app must never overwhelm, never rush, and never scare the user.

3️⃣ Target Audience

All age groups

Indian users (initial launch)

Multiple Indian languages

Includes:

Students

Working professionals

Elderly users

Spiritually inclined users

People with anxiety, stress, depression, loneliness

4️⃣ App Structure (LOCKED)
Bottom Navigation Pages

Home

Chat

Calm (Devotional)

Reflect

Support

Profile

5️⃣ Feature Breakdown (LOCKED)
🏠 Home

Breathing exercises

Anxiety grounding tools

Nature & ambient sounds

Calm music

Panic / emergency button

Instant access to chat

Purpose: Immediate relief without navigation.

💬 Chat

AI Counselor (guide mode)

AI Companion (buddy mode)

Automatic crisis-safe behavior

Important:

AI does NOT diagnose

AI does NOT prescribe

Crisis detection is handled by backend logic, not the LLM alone

🌿 Calm (Devotional Corner)

Devotional music (all religions)

Spiritual chants & recitations

Audiobooks:

Gita

Mahabharata

Quran

Bible

Gurbani

Calm spiritual sounds

Religion & language selection

Purpose: Especially designed for elderly and spiritually inclined users.

🪞 Reflect

Mood check-ins

Text journaling

Voice journaling

Self-reflection prompts

Emotional pattern insights (non-diagnostic)

🆘 Support

Panic / crisis guidance

Mental health helplines (India)

Contact real psychiatrist:

Chat

Call

Video

Trusted contacts

Emergency resources

👤 Profile

Language selection

Privacy & data controls

AI memory on/off

Devotional preferences

Ethics & disclaimers

6️⃣ Onboarding Flow (NEW USERS ONLY)

Sign up / login

Language selection

Consent & disclaimer

Optional emotional understanding test
(NOT called trauma test, NOT diagnostic)

Test is used internally to:

Adjust AI tone

Increase crisis sensitivity

Personalize support

User can skip anytime.

7️⃣ Technical Stack (LOCKED)
Backend

Node.js

Fastify

Prisma ORM

Database

Supabase PostgreSQL

Single database (no Mongo, no Redis in v1)

AI

Self-hosted Gemma 2 (9B) via Ollama

Streaming responses

Backend-controlled prompts

Crisis-safe response layer

APIs

REST (primary)

WebSockets (chat & crisis)

Hosting

Backend: Render / Fly.io

Database: Supabase

Media: Supabase Storage / CDN

Android

Flutter

minSdkVersion = 23

8️⃣ Architecture Principles (LOCKED)

Single backend for Web + Android

Modular monolith (v1)

Crisis paths isolated logically

No Kafka

No Redis (v1)

No serverless-only backend

PostgreSQL used for:

Users

Consent

Journals

Crisis events

Psychiatrist sessions

Audit logs

9️⃣ Compliance & Ethics (MANDATORY)

India-only compliance:

DPDP Act, 2023

IT Act + SPDI Rules

Telemedicine Guidelines (for psychiatrists)

Mental Healthcare Act (principles)

Hard rules:

No diagnosis

No medication advice

No coercive language

No auto-contacting anyone without consent

No admin access to private emotions

🔟 Performance Goals

Home actions < 100ms

Panic button < 200ms

Chat first response < 500ms

Audio always served via CDN

Async everything non-user-facing

1️⃣1️⃣ UI / UX Direction

Calm, dark, low-contrast design

Large cards

Minimal text

Soft animations

Inspired by Calm-style UI

Accessibility-first

🚨 MOST IMPORTANT INSTRUCTION FOR THE CODING AGENT

DO NOT implement, change, optimize, or generate code unless explicitly instructed.

The agent must:

Ask before acting

Follow instructions exactly

Never assume missing requirements

Never add features on its own

Never refactor without permission

This project is instruction-driven, not autonomous.

🏁 Final Summary (One Line)

A.R.O.U.R.A is a calm, safety-first, India-focused mental health support platform using AI, devotion, and human care—built with Node.js, Fastify, Prisma, Supabase, and strict ethical boundaries.