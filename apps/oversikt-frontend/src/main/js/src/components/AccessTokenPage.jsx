import React from 'react';
import {useParams} from "react-router-dom";
import {FetchAccessToken} from "./FetchAccessToken";
import Page from "./Page";

const AccessTokenPage = () => {
    const {name} = useParams()
    return (
        <Page title={name} paragraph={"Hent token for OnBehalfOf-flow"}>
            <FetchAccessToken key={name} name={name}/>
        </Page>
    )
};

export default AccessTokenPage