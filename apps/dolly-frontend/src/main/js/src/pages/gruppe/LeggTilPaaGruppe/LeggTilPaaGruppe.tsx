import Button from "@/components/ui/button/Button";
import React from "react";

export const LeggTilPaaGruppe = ({antallPersoner}) => {
    return (
        <Button
            onClick={null}
            kind="add-circle"
            disabled={antallPersoner < 1}
            title={antallPersoner < 1 ? 'Kan ikke legge til på en tom gruppe' : null}
        >
            LEGG TIL
        </Button>
    )
}