import {Box, VStack} from "@navikt/ds-react";
import React from "react";
import {Jobbstatus} from "@/pages/adminPages/Appstyring/util/Typer";


export const StatusBox = (data: Jobbstatus) => {

    const renderedBox = (status: boolean) => {
        if (!status) {
            return <Box padding="4" background="surface-alt-3-subtle">
                Ingen aktiv jobb
            </Box>
        }
        return <Box padding="4" background="surface-success-subtle">
            Aktiv jobb: Neste kjøring vil starte {data.nesteKjoring}
        </Box>
    }

    return (
        <VStack gap="4">
            {renderedBox(data.status)}
        </VStack>
    );
};