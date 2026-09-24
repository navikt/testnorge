package no.nav.dolly.synt.tilleggsstonad.onnx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TilleggsstonadType {

    BOUTGIFT("boutgifter", "BOUTGIFTER"),
    BOUTGIFTER_ARBEIDSSOKERE("boutgifter_arbeidssokere", "BOUTGIFTER"),
    DAGLIG_REISE("daglig_reise", "DAGLIG_REISE"),
    DAGLIG_REISE_ARBEIDSSOKER("daglig_reise_arbeidssoker", "DAGLIG_REISE"),
    LAEREMIDLER("laeremidler", "LAEREMIDLER"),
    LAEREMIDLER_ARBEIDSSOKERE("laeremidler_arbeidssokere", "LAEREMIDLER"),
    FLYTTING("flytting", "FLYTTING"),
    FLYTTING_ARBEIDSSOKERE("flytting_arbeidssokere", "FLYTTING"),
    REISE_AKTIVITET_OG_HJEMREISER("reise_aktivitet_og_hjemreiser", "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE"),
    REISE_AKTIVITET_OG_HJEMREISER_ARBEIDSSOKERE("reise_aktivitet_og_hjemreiser_arbeidssokere", "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE"),
    REISESTONAD_TIL_ARBEIDSSOKERE("reisestonad_til_arbeidssokere", "REISESTONAD_ARBEIDSSOKER"),
    REISE_TIL_OBLIGATORISK_SAMLING("reise_til_obligatorisk_samling", "REISE_OBLIGATORISK_SAMLING"),
    REISE_TIL_OBLIGATORISK_SAMLING_ARBEIDSSOKERE("reise_til_obligatorisk_samling_arbeidssokere", "REISE_OBLIGATORISK_SAMLING"),
    TILSYN_FAMILIEMEDLEMMER("tilsyn_familiemedlemmer", "TILSYN_FAMILIEMEDLEMMER"),
    TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE("tilsyn_familiemedlemmer_arbeidssokere", "TILSYN_FAMILIEMEDLEMMER"),
    TILSYN_BARN("tilsyn_barn", "TILSYN_BARN"),
    TILSYN_BARN_ARBEIDSSOKER("tilsyn_barn_arbeidssoker", "TILSYN_BARN");

    @Getter
    private final String modelName;

    @Getter
    private final String resultField;

}
