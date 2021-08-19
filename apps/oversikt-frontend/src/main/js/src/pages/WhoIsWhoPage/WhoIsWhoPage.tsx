import React, {useState} from "react";
import styled from "styled-components";
import {Page} from "@navikt/dolly-komponenter";
import { Hovedknapp } from "nav-frontend-knapper";
import {Search} from "@/components/search";

const WhoIsWhoPage = styled(Page)`
  display: flex;
  justify-content: center;
`;

export default () => {
    const [search, setSearch] = useState("");

    return (
        <WhoIsWhoPage>
            <h1>Hvem er hvem?</h1>
            <Search
                onSearch={() => ""}
                labels={{
                    label: "Søk etter OID:",
                    button: "Søk",
                    onFound: "OID funnet",
                    onNotFound: "OID ikkt funnet",
                    onError: "Noe gikk galt"
                }}
            />
        </WhoIsWhoPage>
    );
};
