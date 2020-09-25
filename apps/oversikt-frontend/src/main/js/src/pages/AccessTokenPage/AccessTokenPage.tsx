import React from "react";
import PageWithMenu from "@/components/PageWithMenu";
import FetchAccessToken from "@/components/FetchAccessToken";
import {useParams} from "react-router-dom";

type Props = {
    navigations: Navigation[]
}

const AccessTokenPage = ({navigations}: Props) => {
    // @ts-ignore
    const {name} = useParams()
    return (
        <PageWithMenu title="Access Token" navigations={navigations} menuTitle="Applikasjoner">
            <h2>{name}</h2>
            <p>Hent personlige token for OnBehalfOf-flow</p>
            <FetchAccessToken name={name}/>
        </PageWithMenu>
    )
}

export default AccessTokenPage;
