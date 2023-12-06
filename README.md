# elasticsearch-genai-demo

This repository contains the source code for my talk at [Open Source Experience](https://www.opensource-experience.com/), Paris, in December 2023. 

The [slides are also available](https://docs.google.com/presentation/d/e/2PACX-1vTY938dNVhbmbX7nqAGXuz73td8s6YpdcliEppFTuyRUSZGuC_e2LkDGqydwVPdWWQgdKpodxfbBRo3/pub?start=false).


## Installation

This demo requires two additional tools:
* [Elasticsearch](https://www.elastic.co/elasticsearch). Start it using `docker compose up`.
* [Ollama](https://ollama.ai/) with the Mistral model. Install Ollama and and run `ollama pull mistral`.

## Demos

The first demo indexes the contexts of `data/workplace-documents.json` as vectors in Elasticsearch. Start it by running`./gradlew index-data`.

The second demo is a Q&A command-line interface. Start it by running `./gradlew search-bot -q --console=plain`.

A good question is "What is NASA?". The search bot will anwser explaining the Americas sales regions:

> NASA stands for North America South America region. It is a region within the sales organization of a company that includes the United States, Canada, Mexico, as well as Central and South America. The region has two Area Vice-Presidents: Laura Martinez and Gary Johnson. They are responsible for managing the sales operations in this region and identifying opportunities to expand the presence of the company and better serve its customers in this region.

The same question with the Mistral LLM (run it with `ollama run mistral) explains the space agency:

> NASA stands for National Aeronautics and Space Administration. It is a United States government agency responsible for space exploration, aeronautics, and
the development of space technology. NASA was established in 1958...
