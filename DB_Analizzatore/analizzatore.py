import json
import time

def count_tables(filename):
    with open(filename, 'r', encoding="utf-8") as file:
        return sum(1 for _ in file)

def average_rows(filename):
    total_rows = 0
    num_tables = count_tables(filename)
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            total_rows += table["maxDimensions"]["row"] + 1  # Aggiungi 1 perché inizia da 0
    
    return total_rows / num_tables

def average_columns(filename):
    total_columns = 0
    num_tables = count_tables(filename)
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            total_columns += table["maxDimensions"]["column"] + 1  # Aggiungi 1 perché inizia da 0
    
    return total_columns / num_tables

def distribution_rows(filename):
    distribution = {
        '1-10': 0,
        '11-100': 0,
        '101-1000': 0,
        '1001+': 0
    }
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            rows = table["maxDimensions"]["row"] + 1
            
            if 1 <= rows <= 10:
                distribution['1-10'] += 1
            elif 11 <= rows <= 100:
                distribution['11-100'] += 1
            elif 101 <= rows <= 1000:
                distribution['101-1000'] += 1
            else:
                distribution['1001+'] += 1

    return distribution

def distribution_columns(filename):
    distribution = {
        '1-10': 0,
        '11-100': 0,
        '101-1000': 0,
        '1001+': 0
    }
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            columns = table["maxDimensions"]["column"] + 1
            
            if 1 <= columns <= 10:
                distribution['1-10'] += 1
            elif 11 <= columns <= 100:
                distribution['11-100'] += 1
            elif 101 <= columns <= 1000:
                distribution['101-1000'] += 1
            else:
                distribution['1001+'] += 1

    return distribution

def distribution_distinct_values(filename):
    distribution = {
        '1-2': 0,
        '3-20': 0,
        '21-100': 0,
        '101-1000': 0,
        '1001+': 0
    }
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            columns = table["maxDimensions"]["column"] + 1
            
            for col in range(columns):
                distinct_values = set()
                for cell in table["cells"]:
                    if cell["Coordinates"]["column"] == col:
                        distinct_values.add(cell["cleanedText"])
                
                count_distinct = len(distinct_values)
                if 1 <= count_distinct <= 2:
                    distribution['1-2'] += 1
                elif 3 <= count_distinct <= 20:
                    distribution['3-20'] += 1
                elif 21 <= count_distinct <= 100:
                    distribution['21-100'] += 1
                elif 101 <= count_distinct <= 1000:
                    distribution['101-1000'] += 1
                else:
                    distribution['1001+'] += 1

    return distribution



def average_null_values(filename):
    total_null_values = 0
    num_tables = count_tables(filename)
    
    with open(filename, 'r', encoding="utf-8") as file:
        for line in file:
            table = json.loads(line)
            null_values = sum(1 for cell in table["cells"] if not cell["cleanedText"])
            total_null_values += null_values
    
    return total_null_values / num_tables


def print_with_time(func, message, *args):
    start_time = time.time()
    result = func(*args)
    elapsed_time = time.time() - start_time
    
    minutes = int(elapsed_time // 60)
    seconds = int(elapsed_time % 60)
    
    print(f"{message}: {result} [Tempo di ricerca: {minutes}m {seconds}s]")

filename = "tables.json"
print_with_time(count_tables, f"Numero di tabelle nel file {filename}", filename)
print_with_time(average_rows, "Numero medio di righe", filename)
print_with_time(average_columns, "Numero medio di colonne", filename)
print_with_time(distribution_rows, "Distribuzione numero di righe", filename)
print_with_time(distribution_columns, "Distribuzione numero di colonne", filename)
print_with_time(average_null_values, "Numero medio di valori nulli per tabella", filename)
print_with_time(distribution_distinct_values, "Distribuzione valori distinti per colonna", filename)
