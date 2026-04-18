import os
from pathlib import Path
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parents[6]
DATA_DIR = PROJECT_ROOT / "data" / "results"
OUT_DIR = SCRIPT_DIR / "output"
OUT_DIR.mkdir(parents=True, exist_ok=True)

COLORS  = {"Naive": "#e05353", "UnionRank": "#4a90d9", "FullTarjan": "#27ae60"}
MARKERS = {"Naive": "o", "UnionRank": "s", "FullTarjan": "^"}


def savefig(name):
    path = OUT_DIR / name
    plt.savefig(path, dpi=150, bbox_inches="tight")
    plt.close()
    print(f"  {path}")


def fmt_n(x, _):
    if x >= 1_000_000:
        return f"{x/1_000_000:.0f}M"
    if x >= 1_000:
        return f"{x/1_000:.0f}k"
    return str(int(x))


def _log_grid(ax):
    ax.grid(True, which="both", linestyle="--", alpha=0.4)
    ax.xaxis.set_major_formatter(mticker.FuncFormatter(fmt_n))


def plot_kruskal():
    df = pd.read_csv(DATA_DIR / "kruskal.csv")
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

    for v, g in df.groupby("variant"):
        g = g.sort_values("n")
        c, m = COLORS[v], MARKERS[v]
        ax1.plot(g["n"], g["median_time_ms"], label=v, color=c, marker=m)
        eps = 1e-9
        lower = (g["median_time_ms"] - g["std_time_ms"]).clip(lower=eps)
        upper = (g["median_time_ms"] + g["std_time_ms"]).clip(lower=eps)
        ax1.fill_between(g["n"], lower, upper, alpha=0.15, color=c)
        ax2.plot(g["n"], g["avg_accesses"], label=v, color=c, marker=m)

    ax1.set_xscale("log")
    ax1.set_yscale("log")
    ax1.set_xlabel("n (vértices)")
    ax1.set_ylabel("Tempo mediano (ms)")
    ax1.set_title("Kruskal MST — Tempo de execução")
    ax1.legend()
    _log_grid(ax1)

    ax2.set_xscale("log")
    ax2.set_yscale("log")
    ax2.set_xlabel("n (vértices)")
    ax2.set_ylabel("Acessos ao parent[] (média)")
    ax2.set_title("Kruskal MST — Acessos ao parent[]")
    ax2.legend()
    _log_grid(ax2)

    plt.tight_layout()
    savefig("kruskal.png")


def plot_stress():
    df = pd.read_csv(DATA_DIR / "stress.csv")
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

    for v, g in df.groupby("variant"):
        g = g.sort_values("n")
        c, m = COLORS[v], MARKERS[v]
        ax1.plot(g["n"], g["median_time_ms"], label=v, color=c, marker=m)
        ax2.plot(g["n"], g["avg_accesses"],   label=v, color=c, marker=m)

    ax1.set_xscale("log")
    ax1.set_yscale("log")
    ax1.set_xlabel("n (vértices)")
    ax1.set_ylabel("Tempo mediano (ms)")
    ax1.set_title("Stress Queries — Tempo")
    ax1.legend()
    _log_grid(ax1)

    ax2.set_xscale("log")
    ax2.set_yscale("log")
    ax2.set_xlabel("n (vértices)")
    ax2.set_ylabel("Acessos ao parent[] (média)")
    ax2.set_title("Stress Queries — Acessos ao parent[]")
    ax2.legend()
    _log_grid(ax2)

    plt.tight_layout()
    savefig("stress.png")


def plot_e1():
    df = pd.read_csv(DATA_DIR / "e1.csv").sort_values("n")
    n  = df["n"].values
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

    ax1.plot(n, df["pointer_accesses"], color=COLORS["Naive"], marker="o", label="Naive")
    scale = df["pointer_accesses"].values[0] / n[0] ** 2
    ax1.plot(n, scale * n ** 2, "k--", label="O(n²) ref")
    ax1.set(xscale="log", yscale="log",
            xlabel="n", ylabel="Acessos totais ao parent[]",
            title="E1 — Pior caso Naive: acessos totais")
    ax1.legend(); _log_grid(ax1)

    ax2.plot(n, df["avg_path_length"], color=COLORS["Naive"], marker="o", label="Naive")
    ax2.plot(n, n / 2, "k--", label="n/2 ref")
    ax2.set(xscale="log", yscale="log",
            xlabel="n", ylabel="Nós por Find (média)",
            title="E1 — Comprimento médio do caminho")
    ax2.legend(); _log_grid(ax2)

    plt.tight_layout()
    savefig("e1_naive_worstcase.png")


def plot_e2():
    df = pd.read_csv(DATA_DIR / "e2.csv").sort_values("n")
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

    ax1.plot(df["n"], df["max_height"], color=COLORS["UnionRank"],
             marker="s", label="Altura real (Union by Rank)")
    ax1.plot(df["n"], df["theoretical_max_height"], "k--", label="log₂(n) teórico")
    ax1.set_xscale("log", base=2)
    ax1.set_xlabel("n (escala log₂)")
    ax1.set_ylabel("Altura da árvore")
    ax1.set_title("E2 — Altura real vs. limite teórico log₂(n)")
    ax1.legend(); _log_grid(ax1)

    ax2.plot(df["n"], df["avg_path_length"], color=COLORS["UnionRank"],
             marker="s", label="Nós por Find")
    ax2.plot(df["n"], df["theoretical_max_height"] + 1, "k--", label="log₂(n)+1 teórico")
    ax2.set_xscale("log", base=2)
    ax2.set_xlabel("n (escala log₂)")
    ax2.set_ylabel("Nós visitados por Find (média)")
    ax2.set_title("E2 — Custo por Find")
    ax2.legend(); _log_grid(ax2)

    plt.tight_layout()
    savefig("e2_union_rank_height.png")


def plot_e3():
    df = pd.read_csv(DATA_DIR / "e3.csv")
    fig, axes = plt.subplots(1, 3, figsize=(18, 5))

    for v, g in df.groupby("variant"):
        g = g.sort_values("n")
        axes[0].plot(g["n"], g["median_ms"], label=v, color=COLORS[v], marker=MARKERS[v])

    axes[0].set_xscale("log")
    axes[0].set_yscale("log")
    axes[0].set_xlabel("n")
    axes[0].set_ylabel("Tempo mediano (ms)")
    axes[0].set_title("E3 — Tempo por variante")
    axes[0].legend()
    _log_grid(axes[0])

    for v in ["UnionRank", "FullTarjan"]:
        g = df[df["variant"] == v].copy()
        g["speedup_vs_naive"] = pd.to_numeric(g["speedup_vs_naive"], errors="coerce")
        g = g.dropna(subset=["speedup_vs_naive"]).sort_values("n")
        axes[1].plot(g["n"], g["speedup_vs_naive"],
                     label=v, color=COLORS[v], marker=MARKERS[v])

    axes[1].set_xscale("log")
    axes[1].set_yscale("log")
    axes[1].set_xlabel("n")
    axes[1].set_ylabel("Speedup (×)")
    axes[1].set_title("E3 — Ganho vs. Naive")
    axes[1].legend()
    _log_grid(axes[1])

    ft = df[df["variant"] == "FullTarjan"].copy()
    ft["speedup_vs_union_rank"] = pd.to_numeric(ft["speedup_vs_union_rank"], errors="coerce")
    ft = ft.dropna(subset=["speedup_vs_union_rank"]).sort_values("n")
    axes[2].plot(ft["n"], ft["speedup_vs_union_rank"],
                 color=COLORS["FullTarjan"], marker="^", label="FullTarjan vs UnionRank")
    axes[2].axhline(1, color="gray", linestyle="--", linewidth=1)
    axes[2].set_xscale("log")
    axes[2].set_xlabel("n")
    axes[2].set_ylabel("Speedup (×)")
    axes[2].set_title("E3 — FullTarjan vs. UnionRank")
    axes[2].legend()
    _log_grid(axes[2])

    plt.tight_layout()
    savefig("e3_mixed_ops.png")


def plot_e4():
    df = pd.read_csv(DATA_DIR / "e4.csv")
    ns = sorted(df["n"].unique())
    fig, axes = plt.subplots(2, len(ns), figsize=(4 * len(ns), 8))

    for col, n_val in enumerate(ns):
        sub = df[df["n"] == n_val]
        for v, g in sub.groupby("variant"):
            g = g.sort_values("pass")
            c, m = COLORS[v], MARKERS[v]
            axes[0][col].plot(g["pass"], g["time_ms"],         label=v, color=c, marker=m)
            axes[1][col].plot(g["pass"], g["avg_path_length"], label=v, color=c, marker=m)

        label = f"n={n_val:,}" if n_val < 1_000_000 else f"n={n_val//1_000_000}M"
        axes[0][col].set_title(label)
        for ax in axes[:, col]:
            ax.set_xticks([1, 2, 3])
            ax.grid(True, linestyle="--", alpha=0.4)
        axes[1][col].set_xlabel("Passagem")

        if col == 0:
            axes[0][col].set_ylabel("Tempo (ms)")
            axes[1][col].set_ylabel("Nós por Find (média)")
        axes[0][col].legend(fontsize=8)

    fig.suptitle("E4 — Efeito da compressão de caminho ao longo de 3 passagens", fontsize=13, y=1.02)
    plt.tight_layout()
    savefig("e4_path_compression.png")


if __name__ == "__main__":
    print("gerando gráficos...\n")
    plot_kruskal()
    plot_stress()
    plot_e1()
    plot_e2()
    plot_e3()
    plot_e4()
    print(f"\nsalvo em: {OUT_DIR}")
