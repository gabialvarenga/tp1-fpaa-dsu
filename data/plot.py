"""
Gerador de Gráficos - Análise Comparativa DSU (Disjoint Set Union)
TP1 - Fundamentos de Projeto e Análise de Algoritmos - PUC Minas
"""

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import os
from pathlib import Path

# Configurações visuais
COLORS = {
    "Naive":      "#e05252",   # vermelho
    "UnionRank":  "#4a90d9",   # azul
    "FullTarjan": "#2ecc71",   # verde
}
MARKERS = {"Naive": "o", "UnionRank": "s", "FullTarjan": "^"}
LINESTYLES = {"Naive": "--", "UnionRank": "-.", "FullTarjan": "-"}

plt.rcParams.update({
    "figure.dpi": 150,
    "font.family": "DejaVu Sans",
    "axes.spines.top": False,
    "axes.spines.right": False,
    "axes.grid": True,
    "grid.alpha": 0.3,
    "axes.titlesize": 13,
    "axes.labelsize": 11,
    "legend.fontsize": 10,
})

SCRIPT_DIR = Path(__file__).resolve().parent
RESULTS_DIR = SCRIPT_DIR / "results"
OUTPUT_DIR = SCRIPT_DIR / "graficos_dsu"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def save(fig, name):
    path = os.path.join(OUTPUT_DIR, name)
    fig.savefig(path, bbox_inches="tight")
    plt.close(fig)
    print(f"  ✓  {path}")


# Carrega CSVs
e1 = pd.read_csv(RESULTS_DIR / "e1.csv")
e2 = pd.read_csv(RESULTS_DIR / "e2.csv")
e3 = pd.read_csv(RESULTS_DIR / "e3.csv")
e4 = pd.read_csv(RESULTS_DIR / "e4.csv")
kruskal = pd.read_csv(RESULTS_DIR / "kruskal.csv")
stress = pd.read_csv(RESULTS_DIR / "stress.csv")

print("Gerando gráficos…\n")

# FIGURA 1 — E1: Pior caso do Naive (cadeia linear) — O(n²)
fig, axes = plt.subplots(1, 2, figsize=(13, 5))
fig.suptitle("E1 — Pior Caso DSU Naive (Cadeia Linear)", fontweight="bold")

ax = axes[0]
ax.plot(e1["n"], e1["pointer_accesses"], color=COLORS["Naive"],
        marker="o", lw=2, label="Acessos Totais (Naive)")
# curva teórica O(n²)
ns = np.linspace(e1["n"].min(), e1["n"].max(), 300)
scale = e1["pointer_accesses"].iloc[-1] / (e1["n"].iloc[-1] ** 2)
ax.plot(ns, scale * ns**2, "k--", alpha=0.5, lw=1.5, label="Curva O(n²) teórica")
ax.set_title("Total de Acessos vs n")
ax.set_xlabel("n (número de nós)")
ax.set_ylabel("Acessos ao array parent[]")
ax.yaxis.set_major_formatter(ticker.FuncFormatter(lambda x, _: f"{x/1e9:.1f}B" if x >= 1e9 else f"{x/1e6:.0f}M"))
ax.legend()

ax = axes[1]
ax.plot(e1["n"], e1["avg_path_length"], color=COLORS["Naive"],
        marker="o", lw=2, label="Nós por Find (Naive)")
ax.plot(e1["n"], e1["n"] / 2, "k--", alpha=0.5, lw=1.5, label="Curva n/2 teórica")
ax.set_title("Comprimento Médio de Caminho por Find")
ax.set_xlabel("n (número de nós)")
ax.set_ylabel("Nós visitados por Find")
ax.legend()

plt.tight_layout()
save(fig, "01_e1_naive_pior_caso.png")

# FIGURA 2 — E2: Union by Rank — confirmação de O(log n)
fig, ax = plt.subplots(figsize=(8, 5))
ax.set_title("E2 — Union by Rank: Altura da Árvore vs log₂(n)", fontweight="bold")
ax.plot(e2["n"], e2["max_height"], color=COLORS["UnionRank"],
        marker="s", lw=2, label="Altura Real")
ax.plot(e2["n"], e2["theoretical_max_height"], "k--", alpha=0.6, lw=1.5,
        label="Altura Teórica = log₂(n)")
ax.plot(e2["n"], e2["avg_path_length"], color="#f39c12",
        marker="^", lw=2, linestyle=":", label="Nós por Find (= altura + 1)")
ax.set_xscale("log", base=2)
ax.set_xlabel("n (número de nós) — escala log₂")
ax.set_ylabel("Altura / Nós por Find")
ax.set_title("E2 — Union by Rank: Altura Real = ⌊log₂(n)⌋ (confirmação empírica)", fontweight="bold")
ax.legend()
plt.tight_layout()
save(fig, "02_e2_union_rank_log_n.png")

# FIGURA 3 — E3: Operações Mistas — Tempo mediano (escala log-log)
fig, axes = plt.subplots(1, 2, figsize=(14, 6))
fig.suptitle("E3 — Operações Mistas (60% Union / 40% Find)", fontweight="bold")

# (a) Tempo total
ax = axes[0]
for variant, grp in e3.groupby("variant"):
    ax.plot(grp["n"], grp["median_ms"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2, label=variant)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (número de elementos) — escala log")
ax.set_ylabel("Tempo mediano (ms) — escala log")
ax.set_title("(a) Tempo Total de Execução")
ax.legend()

# (b) Custo por operação (ns/op)
ax = axes[1]
for variant, grp in e3.groupby("variant"):
    ax.plot(grp["n"], grp["ns_per_op"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2, label=variant)
ax.set_xscale("log")
ax.set_xlabel("n (número de elementos) — escala log")
ax.set_ylabel("Custo por operação (ns/op)")
ax.set_title("(b) Custo Amortizado por Operação")
ax.legend()

plt.tight_layout()
save(fig, "03_e3_ops_mistas_tempo.png")

# FIGURA 4 — E3: Speedup — Naive como baseline
e3_speedup = e3[e3["speedup_vs_naive"].notna()].copy()
naive_ns = e3[e3["variant"] == "Naive"][["n", "ns_per_op"]].set_index("n")

fig, ax = plt.subplots(figsize=(9, 5))
for variant in ["UnionRank", "FullTarjan"]:
    grp = e3_speedup[e3_speedup["variant"] == variant]
    ax.plot(grp["n"], grp["speedup_vs_naive"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2, label=variant)

# anotação pico
for variant in ["UnionRank", "FullTarjan"]:
    grp = e3_speedup[e3_speedup["variant"] == variant]
    idx = grp["speedup_vs_naive"].idxmax()
    row = grp.loc[idx]
    ax.annotate(f'{row["speedup_vs_naive"]:.0f}×',
                xy=(row["n"], row["speedup_vs_naive"]),
                xytext=(10, 5), textcoords="offset points",
                fontsize=9, color=COLORS[variant], fontweight="bold")

ax.set_xscale("log")
ax.set_xlabel("n (número de elementos) — escala log")
ax.set_ylabel("Speedup vs Naive")
ax.set_title("E3 — Speedup em relação ao DSU Naive (operações mistas)", fontweight="bold")
ax.legend()
plt.tight_layout()
save(fig, "04_e3_speedup_vs_naive.png")

# FIGURA 5 — E4: Efeito isolado da Compressão de Caminho (CORRIGIDO)
from matplotlib.patches import Patch
 
fig, axes = plt.subplots(1, 2, figsize=(15, 6))
fig.suptitle("E4 — Efeito da Compressão de Caminho (Path Compression)", fontweight="bold")
 
n_vals = sorted(e4["n"].unique())
x = np.arange(len(n_vals))
width = 0.13
passes = [1, 2, 3]
pass_colors = ["#4a90d9", "#2ecc71", "#e67e22"]
# Hachuras: UnionRank = sem hachura, FullTarjan = hachura "///"
hatches = {"UnionRank": "", "FullTarjan": "///"}
variant_order = ["UnionRank", "FullTarjan"]
 
ax = axes[0]
for i, variant in enumerate(variant_order):
    grp_v = e4[e4["variant"] == variant]
    offset_base = (i - 0.5) * (width * 3 + 0.05)
    for j, p in enumerate(passes):
        grp_p = grp_v[grp_v["pass"] == p]
        times = [grp_p[grp_p["n"] == n]["time_ms"].values[0] for n in n_vals]
        ax.bar(x + offset_base + j * width, times, width,
               color=pass_colors[j], alpha=0.85,
               hatch=hatches[variant],
               edgecolor="white" if hatches[variant] == "" else "gray",
               linewidth=0.5)
 
ax.set_xticks(x)
ax.set_xticklabels([f"{n:,}" for n in n_vals], rotation=30, ha="right", fontsize=8)
ax.set_ylabel("Tempo (ms)")
ax.set_title("(a) Tempo por Passagem")
 
legend_elements = [
    Patch(facecolor=pass_colors[0], label="Passagem 1"),
    Patch(facecolor=pass_colors[1], label="Passagem 2"),
    Patch(facecolor=pass_colors[2], label="Passagem 3"),
    Patch(facecolor="#aaa", hatch="",    edgecolor="white", label="UnionRank (sem compressão)"),
    Patch(facecolor="#aaa", hatch="///", edgecolor="gray",  label="FullTarjan (com compressão)"),
]
ax.legend(handles=legend_elements, fontsize=8, loc="upper left")
 
# (b) Comprimento médio do caminho por passagem — com offsets para desempilhar sobreposições
ax = axes[1]
styles   = {"UnionRank": "--", "FullTarjan": "-"}
colors_p = {"UnionRank": COLORS["UnionRank"], "FullTarjan": COLORS["FullTarjan"]}
pass_markers = {1: "o", 2: "s", 3: "^"}
# deslocamentos verticais para separar linhas sobrepostas
offsets = {
    ("UnionRank",  1): 0.0,
    ("UnionRank",  2): 0.35,   # P2 e P3 do UnionRank são iguais -> separa levemente
    ("UnionRank",  3): -0.35,
    ("FullTarjan", 1): 0.0,
    ("FullTarjan", 2): 0.12,   # P2 e P3 do FullTarjan são iguais -> separa levemente
    ("FullTarjan", 3): -0.12,
}
for variant in variant_order:
    grp_v = e4[e4["variant"] == variant]
    for p in passes:
        grp_p = grp_v[grp_v["pass"] == p].sort_values("n")
        dy = offsets[(variant, p)]
        ax.plot(grp_p["n"], grp_p["avg_path_length"] + dy,
                color=colors_p[variant], marker=pass_markers[p],
                linestyle=styles[variant], lw=1.8, alpha=0.9,
                label=f"{variant} — P{p}")
 
# Anotações explicando as sobreposições
ax.annotate("UnionRank P1=P2=P3\n(sem compressão,\ncusto constante entre passagens)",
            xy=(1048576, 11), xytext=(70000, 11.6),
            fontsize=7.5, color=COLORS["UnionRank"],
            arrowprops=dict(arrowstyle="->", color=COLORS["UnionRank"], lw=0.8))
ax.annotate("FullTarjan P2=P3\n(após comprimir na P1,\ncusto fica fixo em ~2)",
            xy=(262144, 2), xytext=(2000, 3.2),
            fontsize=7.5, color=COLORS["FullTarjan"],
            arrowprops=dict(arrowstyle="->", color=COLORS["FullTarjan"], lw=0.8))
 
ax.set_xscale("log", base=2)
ax.set_xlabel("n — escala log₂")
ax.set_ylabel("Nós visitados por Find")
ax.set_title("(b) Comprimento Médio de Caminho por Passagem\n(linhas levemente deslocadas onde há sobreposição)")
 
handles = []
for v in variant_order:
    handles.append(plt.Line2D([0], [0], color=colors_p[v], lw=2,
                               linestyle=styles[v], label=v))
for p in passes:
    handles.append(plt.Line2D([0], [0], color="gray", lw=1.5,
                               marker=pass_markers[p], label=f"Passagem {p}",
                               linestyle="none"))
ax.legend(handles=handles, fontsize=9, loc="upper left")
 
plt.tight_layout()
save(fig, "05_e4_compressao_caminho.png")

# FIGURA 6 — Kruskal: Tempo de execução
fig, axes = plt.subplots(1, 2, figsize=(14, 6))
fig.suptitle("Kruskal (grafo esparso ~3n arestas) — Comparativo de Variantes", fontweight="bold")

ax = axes[0]
for variant, grp in kruskal.groupby("variant"):
    ax.errorbar(grp["n"], grp["median_time_ms"],
                yerr=grp["std_time_ms"],
                color=COLORS[variant], marker=MARKERS[variant],
                linestyle=LINESTYLES[variant], lw=2,
                capsize=4, label=variant)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (vértices) — escala log")
ax.set_ylabel("Tempo mediano (ms) — escala log")
ax.set_title("(a) Tempo de Execução (com desvio padrão)")
ax.legend()

ax = axes[1]
for variant, grp in kruskal.groupby("variant"):
    ax.plot(grp["n"], grp["avg_accesses"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2, label=variant)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (vértices) — escala log")
ax.set_ylabel("Acessos ao parent[] — escala log")
ax.set_title("(b) Acessos à Memória (parent[])")
ax.legend()

plt.tight_layout()
save(fig, "06_kruskal_tempo_acessos.png")

# FIGURA 7 — Stress Test: Tempo e Acessos
fig, axes = plt.subplots(1, 2, figsize=(14, 6))
fig.suptitle("Stress Test — Kruskal + 50×n findSet (reutilização da instância DSU)", fontweight="bold")

ax = axes[0]
for variant, grp in stress.groupby("variant"):
    ax.errorbar(grp["n"], grp["median_time_ms"],
                yerr=grp["std_time_ms"],
                color=COLORS[variant], marker=MARKERS[variant],
                linestyle=LINESTYLES[variant], lw=2,
                capsize=4, label=variant)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elementos) — escala log")
ax.set_ylabel("Tempo mediano (ms) — escala log")
ax.set_title("(a) Tempo de Execução")
ax.legend()

ax = axes[1]
for variant, grp in stress.groupby("variant"):
    ax.plot(grp["n"], grp["avg_accesses"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2, label=variant)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elementos) — escala log")
ax.set_ylabel("Acessos ao parent[] — escala log")
ax.set_title("(b) Acessos à Memória")
ax.legend()

plt.tight_layout()
save(fig, "07_stress_test.png")

# FIGURA 8 — Painel Resumo: Curvas de Complexidade
fig, ax = plt.subplots(figsize=(10, 6))
ax.set_title("Resumo — Curvas de Complexidade por Variante\n(Operações Mistas E3 — ns/operação)", fontweight="bold")

for variant, grp in e3.groupby("variant"):
    ax.plot(grp["n"], grp["ns_per_op"],
            color=COLORS[variant], marker=MARKERS[variant],
            linestyle=LINESTYLES[variant], lw=2.5, label=variant)

# Referências teóricas
n_ref = np.logspace(3, 6.7, 200)
# O(n) normalizado a Naive(n=1000) = 184 ns/op
scale_n = 184 / 1000
ax.plot(n_ref, scale_n * n_ref, color="gray", lw=1, linestyle=":", alpha=0.7, label="O(n) teórico")
# O(log n) normalizado
scale_log = 48 / np.log2(1000)
ax.plot(n_ref, scale_log * np.log2(n_ref), color="#aaa", lw=1, linestyle="-.", alpha=0.7, label="O(log n) teórico")
# O(α(n)) ≈ constante ≈ ~13 ns (FullTarjan estável)
ax.axhline(13, color="#2ecc71", lw=0.8, linestyle=":", alpha=0.5, label="O(α(n)) ≈ constante")

ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (número de elementos) — escala log")
ax.set_ylabel("Custo amortizado por operação (ns) — escala log")

# Anotações das complexidades
ax.text(2e5, scale_n * 2e5 * 0.7, "O(n)", color="gray", fontsize=9, fontstyle="italic")
ax.text(4e5, scale_log * np.log2(4e5) * 1.15, "O(log n)", color="#888", fontsize=9, fontstyle="italic")
ax.text(4e6, 10.5, "O(α(n))", color="#2ecc71", fontsize=9, fontstyle="italic")

ax.legend(loc="upper left")
plt.tight_layout()
save(fig, "08_resumo_complexidade.png")

# FIGURA 9 — Speedup FullTarjan vs UnionRank (E3 + Stress + Kruskal)
fig, axes = plt.subplots(1, 3, figsize=(16, 5))
fig.suptitle("Speedup: FullTarjan vs UnionRank — em cada cenário", fontweight="bold")

# E3
e3_ur = e3[e3["variant"] == "UnionRank"].set_index("n")
e3_ft = e3[e3["variant"] == "FullTarjan"].set_index("n")
common = e3_ur.index.intersection(e3_ft.index)
speedup_e3 = e3_ur.loc[common, "median_ms"] / e3_ft.loc[common, "median_ms"]
axes[0].plot(common, speedup_e3, color="#8e44ad", marker="D", lw=2)
axes[0].axhline(1, color="gray", lw=0.8, linestyle="--")
axes[0].set_xscale("log")
axes[0].set_title("(a) Operações Mistas (E3)")
axes[0].set_xlabel("n")
axes[0].set_ylabel("Speedup (UnionRank / FullTarjan)")

# Kruskal
kr_ur = kruskal[kruskal["variant"] == "UnionRank"].set_index("n")
kr_ft = kruskal[kruskal["variant"] == "FullTarjan"].set_index("n")
common = kr_ur.index.intersection(kr_ft.index)
speedup_kr = kr_ur.loc[common, "median_time_ms"] / kr_ft.loc[common, "median_time_ms"]
axes[1].plot(common, speedup_kr, color="#e67e22", marker="D", lw=2)
axes[1].axhline(1, color="gray", lw=0.8, linestyle="--")
axes[1].set_xscale("log")
axes[1].set_title("(b) Kruskal")
axes[1].set_xlabel("n")

# Stress
st_ur = stress[stress["variant"] == "UnionRank"].set_index("n")
st_ft = stress[stress["variant"] == "FullTarjan"].set_index("n")
common = st_ur.index.intersection(st_ft.index)
speedup_st = st_ur.loc[common, "median_time_ms"] / st_ft.loc[common, "median_time_ms"]
axes[2].plot(common, speedup_st, color="#c0392b", marker="D", lw=2)
axes[2].axhline(1, color="gray", lw=0.8, linestyle="--")
axes[2].set_xscale("log")
axes[2].set_title("(c) Stress Test (50×n findSet)")
axes[2].set_xlabel("n")

plt.tight_layout()
save(fig, "09_speedup_fullTarjan_vs_unionRank.png")

print(f"\n✅  {len(os.listdir(OUTPUT_DIR))} gráficos salvos em '{OUTPUT_DIR}/'")