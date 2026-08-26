from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
from groq import Groq
import os

load_dotenv()

app = Flask(__name__)
CORS(app)

api_key = os.getenv("GROQ_API_KEY")

if not api_key:
    raise ValueError("GROQ_API_KEY not found in .env")

client = Groq(api_key=api_key)


def generate_zero_shot(task):

    prompt = f"""
Answer the following task directly.

Task:
{task}

Give a clear and concise answer.
"""

    response = client.chat.completions.create(
        model="openai/gpt-oss-120b",
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.3
    )

    return response.choices[0].message.content


def generate_few_shot(task):

    prompt = f"""
You are solving a task using examples.

Example 1:
Task: Classify this review: "The product is excellent."
Answer: Positive

Example 2:
Task: Classify this review: "The product is terrible."
Answer: Negative

Now solve the new task using the examples as guidance.

Task:
{task}

Give the best answer.
"""

    response = client.chat.completions.create(
        model="openai/gpt-oss-120b",
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.3
    )

    return response.choices[0].message.content


def generate_explanation_based(task):

    prompt = f"""
Solve the following task carefully.

Task:
{task}

Analyze the important information before giving your answer.
Provide the final answer and a brief explanation.
Do not reveal private chain-of-thought or hidden reasoning.
"""

    response = client.chat.completions.create(
        model="openai/gpt-oss-120b",
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.3
    )

    return response.choices[0].message.content


@app.route("/", methods=["GET"])
def home():

    return jsonify({
        "status": "success",
        "message": "Prompt Engineering Lab backend is running"
    })


@app.route("/compare", methods=["POST"])
def compare():

    data = request.get_json()

    if not data or "task" not in data:
        return jsonify({
            "error": "Task is required"
        }), 400

    task = data["task"].strip()

    if not task:
        return jsonify({
            "error": "Task cannot be empty"
        }), 400

    try:

        zero_shot = generate_zero_shot(task)

        few_shot = generate_few_shot(task)

        explanation_based = generate_explanation_based(task)

        return jsonify({
            "zero_shot": zero_shot,
            "few_shot": few_shot,
            "explanation_based": explanation_based
        })

    except Exception as e:

        return jsonify({
            "error": str(e)
        }), 500


if __name__ == "__main__":

    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True
    )