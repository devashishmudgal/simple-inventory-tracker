# Simple Inventory Tracker (PS No. 25)

A console-based inventory tracker for a small shop, written in Java.

- **Student:** Devashish Mudgal (ERP ID: RU-25-10454)
- **Course:** Data Structures and Algorithms (DSA), Session 2026-27
- **University:** Rungta International Skills University, Bhilai, Chhattisgarh
- **Guide:** Mr. Suryamani Verma

## Repository contents

| Path | Description |
|---|---|
| [`Main.java`](Main.java) | Complete source code (single file, `Main` class) |
| [`docs/Project_Report.pdf`](docs/Project_Report.pdf) | Project report (Word version: [`docs/Project_Report.docx`](docs/Project_Report.docx)) |
| [`docs/Project_PPT.pptx`](docs/Project_PPT.pptx) | Project presentation |

## Features

Menu: `1 Add`, `2 Display`, `3 Search`, `4 Sell`, `5 Low stock`, `6 Sort by quantity`, `7 Exit`

- Every item is stored as an object in an `ArrayList<Item>`
- Item IDs are generated automatically, starting from `I101`
- Quantity can never become negative; price must be greater than 0
- Low stock report: quantity below 5
- Sort by quantity (low to high) using a selection sort written in the program
- Restock: adding an item whose name already exists increases its quantity
- Data is loaded from `inventory.txt` when the program starts and saved when it exits (and after every change)

## How to run

Requires JDK 8 or above.

```
javac Main.java
java Main
```

`inventory.txt` is created automatically in the same folder (format: `ID,Name,Quantity,Price`). Delete it to start with an empty inventory.

## Test cases

| # | Input | Output |
|---|---|---|
| 1 | Option 1: Notebook, qty 50, price 40 | `Item added. ID: I101 \| Notebook \| Qty: 50 \| 40` |
| 2 | I101 has qty 5; option 4: I101, qty 5 | `Stock updated. I101 \| Notebook \| Qty: 0` and `Item is now out of stock.` |
| 3 | I101 has qty 10; option 4: I101, qty 20 | `Insufficient stock. Available: 10` |
| 4 | I101 (qty 50); add Pen 3 @ 10; option 5; option 6 | `Item added. ID: I102 \| Pen \| Qty: 3 \| 10`, `I102 \| Pen \| Qty: 3`, `1. I102 \| Pen \| 3` / `2. I101 \| Notebook \| 50` |

## Concepts used

ArrayList, searching (linear search), sorting (selection sort), methods, file handling, input validation.
