import React, {useState} from 'react';
import './FetchAccessToken.css'
import {CopyToClipboard} from "react-copy-to-clipboard/lib/Component";
import Button from "../Button";
import {fetchToken} from "../../api";


export const FetchAccessToken = ({name}) => {
    const [accessToken, setAccessToken] = useState(null);
    const [loading, setLoading] = useState(false);


    const onGetToken = name => {
        setLoading(true);
        fetchToken(name).then(response => {
            setAccessToken(response.token)
            setLoading(false)
        })
    }

    const getCopyButton = () => (
        <CopyToClipboard text={accessToken}>
            <Button color="secondary" onClick={() => setAccessToken(name)}>Copy</Button>
        </CopyToClipboard>
    )
    return (
        <div>
            <h2>{name}</h2>
            <p>Hent accessToken for OnBehalfOf-flow for {name}.</p>
            <p className="access_token">{loading ? loading : accessToken ? accessToken : ""}</p>
            <Button color="primary" onClick={() => onGetToken(name)}>Hent token</Button>
            {accessToken ? getCopyButton() : null}
        </div>
    )
}