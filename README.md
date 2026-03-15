# TP1 — Disjoint Set Union (Union-Find)

**Disciplina:** Fundamentos de Projeto e Análise de Algoritmos  
**Professor:** João Pedro O. Batisteli  
**PUC Minas — 2026/1**

---

## Descrição

Este projeto consiste na implementação e análise experimental da estrutura de dados **Disjoint Set Union (Union-Find)**, frequentemente utilizada para resolver problemas de componentes conexos em grafos.

---

## Estrutura do Projeto

```
src/
    dsu/         # Implementação da estrutura Union-Find
    benchmark/   # Scripts e códigos para benchmarks
    test/        # Casos de teste automatizados

data/
    inputs/      # Instâncias de entrada para testes e benchmarks
    results/     # Resultados gerados pelos experimentos

scripts/       # Scripts auxiliares para automação
graficos/      # Geração de gráficos dos resultados
artigo/        # Documentação e artigo do projeto
```

---

## Como Compilar

Para compilar o projeto, utilize um dos comandos abaixo:

```sh
make build-O0   # Compilação sem otimizações
make build-O2   # Compilação com otimizações
```

---

## Como Testar

Execute os testes automatizados com:

```sh
make test
```

---

## Como Executar Benchmarks

Para rodar os benchmarks e coletar dados experimentais:

```sh
make benchmark
```

---

## Geração de Gráficos

Para gerar gráficos a partir dos resultados dos benchmarks:

```sh
make graficos
```

---

