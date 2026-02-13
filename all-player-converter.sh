#!/bin/bash

# Usage function
usage() {
  echo "Usage: $0 <batsmen_csv> <bowlers_csv> <wicket_keepers_csv> <all_rounders_csv>"
  echo ""
  echo "Example:"
  echo "  $0 batsmen.csv bowlers.csv keepers.csv allrounders.csv"
  exit 1
}

# Check if all 4 files are provided
if [ $# -ne 4 ]; then
  usage
fi

BATSMEN_FILE=$1
BOWLERS_FILE=$2
KEEPERS_FILE=$3
ALLROUNDERS_FILE=$4

# Validate all files exist
for file in "$BATSMEN_FILE" "$BOWLERS_FILE" "$KEEPERS_FILE" "$ALLROUNDERS_FILE"; do
  if [ ! -f "$file" ]; then
    echo "Error: File '$file' not found"
    exit 1
  fi
done

# Use Python to generate both JSON files
python3 - "$BATSMEN_FILE" "$BOWLERS_FILE" "$KEEPERS_FILE" "$ALLROUNDERS_FILE" << 'PYTHON_SCRIPT'
import csv
import json
import sys

# File paths from bash arguments
batsmen_file = sys.argv[1]
bowlers_file = sys.argv[2]
keepers_file = sys.argv[3]
allrounders_file = sys.argv[4]

game_one_player_data = []
game_one_set = []
player_id = 1

def clean_value(value):
    """Clean and strip whitespace, tabs, and other characters"""
    if value is None:
        return None
    return value.strip().replace('\t', '').replace('\r', '')

def parse_bool(value):
    """Convert Yes/No/yes/no to boolean"""
    cleaned = clean_value(value)
    return cleaned.lower() == 'yes' if cleaned else False

def parse_number(value, is_int=False):
    """Parse number, return None if empty"""
    cleaned = clean_value(value)
    if not cleaned or cleaned == '':
        return None
    try:
        return int(cleaned) if is_int else float(cleaned)
    except ValueError:
        return None

# Process BATSMEN
print(f"Processing batsmen from {batsmen_file}...")
with open(batsmen_file, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        name = clean_value(row['Name'])
        if not name:
            continue
            
        is_legend = parse_bool(row['isLegend'])
        image_link = clean_value(row['Image Link'])
        is_foreign = parse_bool(row['isForeign'])
        is_uncapped = parse_bool(row['isUncapped'])
        points = parse_number(row['points'], is_int=True) or 0
        price = parse_number(row['price']) or 0
        price = int(price * 10000000)  # Convert crores to actual amount
        country = clean_value(row['country'])
        
        # Batsman stats
        runs = parse_number(row['runs'], is_int=True)
        matches = parse_number(row['Matches'], is_int=True)
        average = parse_number(row['Average'])
        strike_rate = parse_number(row['Strike Rate'])
        
        batsman_stats = None
        if runs is not None:
            batsman_stats = {
                "runs": runs,
                "matches": matches,
                "battingAvg": average,
                "strikeRate": strike_rate
            }
        
        player = {
            "id": None,
            "name": name,
            "imageLink": image_link,
            "type": "BATSMAN",
            "isForeign": is_foreign,
            "isUncapped": is_uncapped,
            "isLegend": is_legend,
            "country": country,
            "batsmanStats": batsman_stats,
            "bowlerStats": None,
            "allRounderStats": None
        }
        
        game_one_player_data.append(player)
        
        set_obj = {
            "setId": 1,
            "playerId": player_id,
            "points": points,
            "price": price,
            "order": player_id
        }
        
        game_one_set.append(set_obj)
        player_id += 1

# Process BOWLERS
print(f"Processing bowlers from {bowlers_file}...")
with open(bowlers_file, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        name = clean_value(row['Name'])
        if not name:
            continue
            
        is_legend = parse_bool(row['isLegend'])
        image_link = clean_value(row['Image Link'])
        is_foreign = parse_bool(row['isForeign'])
        is_uncapped = parse_bool(row['isUncapped'])
        points = parse_number(row['points'], is_int=True) or 0
        price = parse_number(row['price']) or 0
        price = int(price * 10000000)
        country = clean_value(row['country'])
        
        # Bowler stats
        wickets = parse_number(row['Wickets'], is_int=True)
        matches = parse_number(row['Matches'], is_int=True)
        best_figures = clean_value(row.get('Best Figures', ''))
        economy = parse_number(row['Economy'])
        
        bowler_stats = None
        if wickets is not None:
            bowler_stats = {
                "wickets": wickets,
                "matches": matches,
                "bestFigures": best_figures,
                "economy": economy
            }
        
        player = {
            "id": None,
            "name": name,
            "imageLink": image_link,
            "type": "BOWLER",
            "isForeign": is_foreign,
            "isUncapped": is_uncapped,
            "isLegend": is_legend,
            "country": country,
            "batsmanStats": None,
            "bowlerStats": bowler_stats,
            "allRounderStats": None
        }
        
        game_one_player_data.append(player)
        
        set_obj = {
            "setId": 1,
            "playerId": player_id,
            "points": points,
            "price": price,
            "order": player_id
        }
        
        game_one_set.append(set_obj)
        player_id += 1

# Process WICKET KEEPERS
print(f"Processing wicket keepers from {keepers_file}...")
with open(keepers_file, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        name = clean_value(row['Name'])
        if not name:
            continue
            
        is_legend = parse_bool(row['isLegend'])
        image_link = clean_value(row['Image Link'])
        is_foreign = parse_bool(row['isForeign'])
        is_uncapped = parse_bool(row['isUncapped'])
        points = parse_number(row['points'], is_int=True) or 0
        price = parse_number(row['price']) or 0
        price = int(price * 10000000)
        country = clean_value(row['country'])  # Note: space before 'country' in CSV
        
        # Batsman stats for wicket keepers
        runs = parse_number(row['Runs'], is_int=True)
        matches = parse_number(row['Matches'], is_int=True)
        average = parse_number(row['Batting Average'])
        strike_rate = parse_number(row['Strike Rate'])
        
        batsman_stats = None
        if runs is not None:
            batsman_stats = {
                "runs": runs,
                "matches": matches,
                "battingAvg": average,
                "strikeRate": strike_rate
            }
        
        player = {
            "id": None,
            "name": name,
            "imageLink": image_link,
            "type": "WICKET_KEEPER",
            "isForeign": is_foreign,
            "isUncapped": is_uncapped,
            "isLegend": is_legend,
            "country": country,
            "batsmanStats": batsman_stats,
            "bowlerStats": None,
            "allRounderStats": None
        }
        
        game_one_player_data.append(player)
        
        set_obj = {
            "setId": 1,
            "playerId": player_id,
            "points": points,
            "price": price,
            "order": player_id
        }
        
        game_one_set.append(set_obj)
        player_id += 1

# Process ALL ROUNDERS
print(f"Processing all-rounders from {allrounders_file}...")
with open(allrounders_file, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        name = clean_value(row['Name'])
        if not name:
            continue
            
        is_legend = parse_bool(row['isLegend'])
        image_link = clean_value(row['Image Link'])
        is_foreign = parse_bool(row['isForeign'])
        is_uncapped = parse_bool(row['isUncapped'])
        points = parse_number(row['points'], is_int=True) or 0
        price = parse_number(row['price']) or 0
        price = int(price * 10000000)
        country = clean_value(row['country'])
        
        # All-rounder stats
        runs = parse_number(row['Runs'], is_int=True)
        matches = parse_number(row['matches'], is_int=True)
        wickets = parse_number(row['wickets'], is_int=True)
        strike_rate = parse_number(row['strike rate'])
        
        allrounder_stats = None
        if runs is not None or wickets is not None:
            allrounder_stats = {
                "runs": runs,
                "wickets": wickets,
                "matches": matches,
                "strikeRate": strike_rate
            }
        
        player = {
            "id": None,
            "name": name,
            "imageLink": image_link,
            "type": "ALL_ROUNDER",
            "isForeign": is_foreign,
            "isUncapped": is_uncapped,
            "isLegend": is_legend,
            "country": country,
            "batsmanStats": None,
            "bowlerStats": None,
            "allRounderStats": allrounder_stats
        }
        
        game_one_player_data.append(player)
        
        set_obj = {
            "setId": 1,
            "playerId": player_id,
            "points": points,
            "price": price,
            "order": player_id
        }
        
        game_one_set.append(set_obj)
        player_id += 1

# Write to files
with open('GameOnePlayerData.json', 'w') as f:
    json.dump(game_one_player_data, f, indent=2)

with open('GameOneSet.json', 'w') as f:
    json.dump(game_one_set, f, indent=2)

print(f"\n✓ Created GameOnePlayerData.json with {len(game_one_player_data)} total players:")
print(f"  - Batsmen, Bowlers, Wicket Keepers, All-rounders combined")
print(f"✓ Created GameOneSet.json with {len(game_one_set)} entries")

PYTHON_SCRIPT

echo ""
echo "Conversion complete!"
