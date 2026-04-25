# TP1 — Disjoint Set Union (Union-Find)

**Disciplina:** Fundamentos de Projeto e Análise de Algoritmos  
**Professor:** João Pedro O. Batisteli  
**PUC Minas — 2026/1**

---

## 1. Visão geral
Este projeto realiza a implementação e a rigorosa análise experimental e teórica da estrutura de dados **Disjoint Set Union (Union-Find)**. Ele avalia métricas vitais das implementações como complexidade de acesso à memória (rank e parent arrays), percursos de nós e máxima altura alcançada por árvores usando cenários de testes controlados (ex: Kruskal para grafos dispersos e testes baseados em chains estruturais). O projeto consolida a visualização desses dados através da plotagem científica de gráficos.

---

## 2. Estrutura de pastas

- `dsu/` -> Módulo principal backend em Java com a arquitetura dos algoritmos experientes.
  - `pom.xml` -> Gerenciador de build e dependências (Maven).
  - `src/main/java/tp1/fpaa/` ->
    - `algorithm/dsu/` -> Implementações do core DSU (`DSUNaive`, `DSUUnionRank`, `DSUFullTarjan`).
    - `algorithm/mst/` -> Implementação de algoritmos anexos como o da Árvore Geradora Mínima (Kruskal).
    - `experiment/` -> Suítes de execução (`DSUCaseRunner`, `MSTBenchmarkRunner`) e criação de entradas como grafos aleatórios iterativos conectados (`RandomConnectedGraphFactory`).
    - `statistics/` -> Agregadores de telemetria algorítmica para contagem controlada de interações da estrutura.
    - `output/` -> Controladores de saída para logging de tela (`Printer`) e gravação local (`CSVExporter`).
- `data/` -> Módulo de Data Science para processamento visual dos resultados coletados em Java.
  - `plot.py` -> Script em Python para leitura e consolidação dos gráficos das curvas e amostras empíricas versus estimativas assintóticas O(n²) e O(log n).
  - `graficos_dsu/` -> Onde são salvos todos os gráficos renderizados finalizados em formato visual.
  - `results/` -> Diretório obrigatório que armazena temporalmente os conjuntos de benchmark brutos dos logs do exportador CSV do Java (`e1.csv`... `stress.csv`).

---

## 3. Convenções de código
- **Nomenclatura (Naming conventions):** Pacotes e classes em Java seguem nomenclatura total em idioma **Inglês** combinando `PascalCase` nas Classes/Interfaces e `camelCase` nas referências/instâncias. Em artefatos locais (Documentos ou scripts de Plot), mistura-se português nas descrições de comentários acadêmicos para fins explicativos.
- **Isolamento de Métricas (Telemetry Pattern):** Métodos da API (`find()`, `union()`) nunca acessam campos de arrays de forma ingênua ou livre; eles consomem getters e setters padronizados (como `readParent()`, `writeParent()`) para garantir disparo do Tracking em `MetricsAggregator` sem quebrar transparência operacional de algoritmos independentes.
- **Contratos Interfaceados:** Padrão abstrato forte. O projeto não usa "classes concretas hardcoded", ele injeta instâncias `DSU` a depender da suíte (Exemplo: implementando o contrato `DSU` via `DSUFullTarjan`).

---

## 4. Stack e dependências principais
- **Linguagem Backend:** Java 17.
- **Build / Packaging Tool:** Maven.
- **Framework de Testes:** JUnit Jupiter (versão `5.9.3`) – focado na qualidade de software automatizada de infra.
- **Linguagem Visual:** Python 3.
- **Libs de Visualização no Python:** `Matplotlib`, `NumPy`, e `Pandas` (Responsáveis pela abstração relacional e formatação da representação da Árvore logarítmica/curvas nos outputs visuais).

---

## 5. Regras obrigatórias (NUNCA MODIFIQUE)
1. **Atravessadores Estritos de DSU Array:** Nunca pule ou remova as funções `readParent()` / `writeParent()` dentro das estruturas Union-Find, nem desligue o check de `metrics != null`. Todo o experimento de complexidade quantitativa O() depende fundamentalmente deste incremento linear injetado em tempo real de execução de memória.
2. **Path Hardcoded do Exportador Python:** O Java baseia sua dump class `CSV_OUTPUT_DIR = "../data/results"` atado de forma explícita. Não altere o target dos CSVExporters e o endereço que o Pandas espera em `plot.py` sem sincronizar as duas pontas do stack ou os plots quebrarão permanentemente.
3. **Capacidade do Range (Seed):** Não modifique sementes Randomísticas em classes base implementadas determinísticas como `SEED = 42L;` no modulo Main sem o consentimento acadêmico completo, isto garante consistência de replicação na comparação entre diferentes rodadas.
4. **Instanciação Inválida:** Qualquer inicializador em `n` da capacidade num range < 1 deve acionar falha irrecuperável (`IllegalArgumentException`).

---

## 6. Padrões de implementação
- **Design Pattern "Strategy":** Uso universal da interface base de comportamento iterativo das variantes performáticas (Estratégias de algoritmos variando da Força Bruta pra Compressão Otimizada).
- **Design Pattern "Factory Method":** Centraliza a criação de instâncias de Grafos Aleatórios Conectados para isolar a complexidade do setup iterativo pseudoaleatório longe do contexto de validação real da Main.
- **Command / Runner Architecture:** Fluxos de teste, execução e log segregados. Entidade gerencia caso real de experimento (`Runners`), outra escreve buffer de saída csv (`CSVExporter`), e finalmente o logging visual em sysout (`Printer`).

---

## 7. Integrações externas
Este software opera independentemente de APIs externas via web e sem o auxílio robusto de Bancos de Dados SQL/NoSQL em nuvem. A retenção da sessão é efetuada localmente utilizando I/O em disco diretamente em formatos simplificados de Tabela Comma-Separadas (`.csv`). Bibliotecas dependentes como Matplotlib são as únicas pontes processuais. 

---

## 8. Comandos úteis

Execute estes scripts para replicar a pipeline localmente (Obs: as chamadas do repositório antigo via `make` estavam desencorajadas pela migração para Maven/Python).

*Navegue até a raiz do módulo java `cd dsu` primeiro.*

**Para compilar os arquivos compilados sem rodar o setup limpo:**
```sh
mvn compile
```

**Para executar a suíte isolada de testes automatizados (JUnit):**
```sh
mvn test
```

**Para produzir dados analíticos locais através de Benchmarks nos DSU / Kruskal:**
```sh
mvn exec:java -Dexec.mainClass="tp1.fpaa.Main" -e
```

**Para gerar análises gráficas do framework (.png / pdf gráficos) na raiz em data/:**
```sh
cd ../data
# Garanta ter um ambiente (ex: venv) previamente instanciado contendo as dependências Python
python plot.py
```
