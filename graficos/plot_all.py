"""
Geração de gráficos dos experimentos DSU/Union-Find.

Pré-requisitos:
    pip install -r requirements.txt

Uso (a partir da pasta graficos/):
    python plot_all.py

Os CSVs são lidos de ../data/results/ e as imagens salvas em ./output/.
"""

import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

DATA_DIR = os.path.join(os.path.dirname(__file__), "..", "data", "results")
OUT_DIR = os.path.join(os.path.dirname(__file__), "output")
os.makedirs(OUT_DIR, exist_ok=True)

COLORS = {"Naive": "#e05353", "UnionRank": "#4a90d9", "FullTarjan": "#27ae60"}
MARKERS = {"Naive": "o", "UnionRank": "s", "FullTarjan": "^"}


def savefig(name):
    path = os.path.join(OUT_DIR, name)
    plt.savefig(path, dpi=150, bbox_inches="tight")
    plt.close()
    print(f"  Salvo: {path}")


def fmt_n(x, _):
    if x >= 1_000_000:
        return f"{x/1_000_000:.0f}M"
    if x >= 1_000:
        return f"{x/1_000:.0f}k"
    return str(int(x))


# ---------------------------------------------------------------------------
# Kruskal — tempo e acessos vs n
# ---------------------------------------------------------------------------
def plot_kruskal():
    df = pd.read_csv(os.path.join(DATA_DIR, "kruskal.csv"))

    fig, axes = plt.subplots(1, 2, figsize=(14, 5))

    for variant, group in df.groupby("variant"):
        c, m = COLORS[variant], MARKERS[variant]
        group = group.sort_values("n")
        axes[0].plot(group["n"], group["median_time_ms"], label=variant, color=c, marker=m)
        axes[0].fill_between(
            group["n"],
            group["median_time_ms"] - group["std_time_ms"],
            (group["median_time_ms"] + group["std_time_ms"]).clip(lower=0),
            alpha=0.15, color=c,
        )
        axes[1].plot(group["n"], group["avg_accesses"], label=variant, color=c, marker=m)

    for ax, ylabel, title in [
        (axes[0], "Tempo mediano (ms)", "Kruskal MST — Tempo de execução"),
        (axes[1], "Acessos ao parent[] (média)", "Kruskal MST — Acessos ao parent[]"),
    ]:
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_xlabel("n (vértices)")
        ax.set_ylabel(ylabel)
        ax.set_title(title)
        ax.legend()
        ax.grid(True, which="both", linestyle="--", alpha=0.4)
        ax.xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    plt.tight_layout()
    savefig("kruskal.png")


# ---------------------------------------------------------------------------
# Stress — tempo e acessos vs n (UnionRank vs FullTarjan)
# ---------------------------------------------------------------------------
def plot_stress():
    df = pd.read_csv(os.path.join(DATA_DIR, "stress.csv"))

    fig, axes = plt.subplots(1, 2, figsize=(14, 5))

    for variant, group in df.groupby("variant"):
        c, m = COLORS[variant], MARKERS[variant]
        group = group.sort_values("n")
        axes[0].plot(group["n"], group["median_time_ms"], label=variant, color=c, marker=m)
        axes[1].plot(group["n"], group["avg_accesses"], label=variant, color=c, marker=m)

    for ax, ylabel, title in [
        (axes[0], "Tempo mediano (ms)", "Stress Queries — Tempo de execução"),
        (axes[1], "Acessos ao parent[] (média)", "Stress Queries — Acessos ao parent[]"),
    ]:
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_xlabel("n (vértices)")
        ax.set_ylabel(ylabel)
        ax.set_title(title)
        ax.legend()
        ax.grid(True, which="both", linestyle="--", alpha=0.4)
        ax.xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    plt.tight_layout()
    savefig("stress.png")


# ---------------------------------------------------------------------------
# E1 — Pior caso Naive: acessos totais e comprimento médio do caminho
# ---------------------------------------------------------------------------
def plot_e1():
    df = pd.read_csv(os.path.join(DATA_DIR, "e1.csv"))
    df = df.sort_values("n")

    fig, axes = plt.subplots(1, 2, figsize=(14, 5))

    # Acessos totais vs n com referência O(n²)
    axes[0].plot(df["n"], df["pointer_accesses"], color=COLORS["Naive"], marker="o", label="Naive")
    n_ref = df["n"].values
    scale = df["pointer_accesses"].values[0] / n_ref[0] ** 2
    axes[0].plot(n_ref, scale * n_ref ** 2, "k--", label="O(n²) referência")
    axes[0].set_xscale("log")
    axes[0].set_yscale("log")
    axes[0].set_xlabel("n")
    axes[0].set_ylabel("Acessos totais ao parent[]")
    axes[0].set_title("E1 — Pior caso Naive: acessos totais")
    axes[0].legend()
    axes[0].grid(True, which="both", linestyle="--", alpha=0.4)
    axes[0].xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    # Nós por Find (comprimento médio) vs n
    axes[1].plot(df["n"], df["avg_path_length"], color=COLORS["Naive"], marker="o", label="Naive")
    axes[1].plot(n_ref, n_ref / 2, "k--", label="n/2 referência")
    axes[1].set_xscale("log")
    axes[1].set_yscale("log")
    axes[1].set_xlabel("n")
    axes[1].set_ylabel("Nós por Find (média)")
    axes[1].set_title("E1 — Comprimento médio do caminho")
    axes[1].legend()
    axes[1].grid(True, which="both", linestyle="--", alpha=0.4)
    axes[1].xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    plt.tight_layout()
    savefig("e1_naive_worstcase.png")


# ---------------------------------------------------------------------------
# E2 — Union by Rank: altura real vs log₂(n) teórico
# ---------------------------------------------------------------------------
def plot_e2():
    df = pd.read_csv(os.path.join(DATA_DIR, "e2.csv"))
    df = df.sort_values("n")

    fig, ax = plt.subplots(figsize=(8, 5))

    ax.plot(df["n"], df["max_height"], color=COLORS["UnionRank"], marker="s",
            label="Altura real (Union by Rank)")
    ax.plot(df["n"], df["theoretical_max_height"], "k--", label="log₂(n) teórico")

    ax.set_xscale("log", base=2)
    ax.set_xlabel("n (escala log₂)")
    ax.set_ylabel("Altura da árvore")
    ax.set_title("E2 — Union by Rank: altura real vs. limite teórico log₂(n)")
    ax.legend()
    ax.grid(True, which="both", linestyle="--", alpha=0.4)
    ax.xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    plt.tight_layout()
    savefig("e2_union_rank_height.png")


# ---------------------------------------------------------------------------
# E3 — Operações mistas: tempo, ns/op e speedup
# ---------------------------------------------------------------------------
def plot_e3():
    df = pd.read_csv(os.path.join(DATA_DIR, "e3.csv"))

    fig, axes = plt.subplots(1, 3, figsize=(18, 5))

    # Tempo vs n
    for variant, group in df.groupby("variant"):
        c, m = COLORS[variant], MARKERS[variant]
        group = group.sort_values("n")
        axes[0].plot(group["n"], group["median_ms"], label=variant, color=c, marker=m)

    axes[0].set_xscale("log")
    axes[0].set_yscale("log")
    axes[0].set_xlabel("n")
    axes[0].set_ylabel("Tempo mediano (ms)")
    axes[0].set_title("E3 — Tempo por variante")
    axes[0].legend()
    axes[0].grid(True, which="both", linestyle="--", alpha=0.4)
    axes[0].xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    # Speedup vs Naive
    for variant in ["UnionRank", "FullTarjan"]:
        grp = df[(df["variant"] == variant) & (df["speedup_vs_naive"].notna()) & (df["speedup_vs_naive"] != "")]
        grp = grp.copy()
        grp["speedup_vs_naive"] = pd.to_numeric(grp["speedup_vs_naive"], errors="coerce")
        grp = grp.dropna(subset=["speedup_vs_naive"]).sort_values("n")
        axes[1].plot(grp["n"], grp["speedup_vs_naive"],
                     label=variant, color=COLORS[variant], marker=MARKERS[variant])

    axes[1].set_xscale("log")
    axes[1].set_yscale("log")
    axes[1].set_xlabel("n")
    axes[1].set_ylabel("Speedup (×)")
    axes[1].set_title("E3 — Ganho vs. Naive")
    axes[1].legend()
    axes[1].grid(True, which="both", linestyle="--", alpha=0.4)
    axes[1].xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    # Speedup FullTarjan vs UnionRank
    grp_ft = df[(df["variant"] == "FullTarjan") & (df["speedup_vs_union_rank"].notna()) & (df["speedup_vs_union_rank"] != "")]
    grp_ft = grp_ft.copy()
    grp_ft["speedup_vs_union_rank"] = pd.to_numeric(grp_ft["speedup_vs_union_rank"], errors="coerce")
    grp_ft = grp_ft.dropna(subset=["speedup_vs_union_rank"]).sort_values("n")
    axes[2].plot(grp_ft["n"], grp_ft["speedup_vs_union_rank"],
                 color=COLORS["FullTarjan"], marker="^", label="FullTarjan vs UnionRank")
    axes[2].axhline(1, color="gray", linestyle="--", linewidth=1)

    axes[2].set_xscale("log")
    axes[2].set_xlabel("n")
    axes[2].set_ylabel("Speedup (×)")
    axes[2].set_title("E3 — FullTarjan vs. UnionRank")
    axes[2].legend()
    axes[2].grid(True, which="both", linestyle="--", alpha=0.4)
    axes[2].xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))

    plt.tight_layout()
    savefig("e3_mixed_ops.png")


# ---------------------------------------------------------------------------
# E4 — Compressão de caminho: tempo e nós por Find por passagem
# ---------------------------------------------------------------------------
def plot_e4():
    df = pd.read_csv(os.path.join(DATA_DIR, "e4.csv"))
    n_values = sorted(df["n"].unique())

    fig, axes = plt.subplots(2, len(n_values), figsize=(4 * len(n_values), 8), sharey="row")

    for col, n_val in enumerate(n_values):
        subset = df[df["n"] == n_val]
        ax_time = axes[0][col]
        ax_path = axes[1][col]

        for variant, group in subset.groupby("variant"):
            c, m = COLORS[variant], MARKERS[variant]
            group = group.sort_values("pass")
            ax_time.plot(group["pass"], group["time_ms"], label=variant, color=c, marker=m)
            ax_path.plot(group["pass"], group["avg_path_length"], label=variant, color=c, marker=m)

        title = f"n={n_val:,}" if n_val < 1_000_000 else f"n={n_val//1_000_000}M"
        ax_time.set_title(title)
        ax_time.set_xticks([1, 2, 3])
        ax_path.set_xticks([1, 2, 3])
        ax_time.grid(True, linestyle="--", alpha=0.4)
        ax_path.grid(True, linestyle="--", alpha=0.4)

        if col == 0:
            ax_time.set_ylabel("Tempo (ms)")
            ax_path.set_ylabel("Nós por Find (média)")
        ax_path.set_xlabel("Passagem")

        if col == len(n_values) - 1:
            ax_time.legend(fontsize=8)
            ax_path.legend(fontsize=8)

    axes[0][0].set_title(f"n={n_values[0]:,}\nTempo (ms)")
    fig.suptitle("E4 — Efeito da compressão de caminho ao longo de 3 passagens", fontsize=13, y=1.02)
    plt.tight_layout()
    savefig("e4_path_compression.png")


# ---------------------------------------------------------------------------
if __name__ == "__main__":
    print("Gerando gráficos...\n")
    plot_kruskal()
    plot_stress()
    plot_e1()
    plot_e2()
    plot_e3()
    plot_e4()
    print(f"\nPronto! Todos os gráficos salvos em: {OUT_DIR}")
