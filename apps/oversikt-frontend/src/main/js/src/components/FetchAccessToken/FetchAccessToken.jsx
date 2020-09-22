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
    return (
        <div>
            <div>
                <textarea className="access_token" disabled={true} value={loading ? "Laster token..." : accessToken ? accessToken : ""}/>
            </div>
            <Button color="primary" onClick={() => onGetToken(name)}>Hent token</Button>
            <CopyToClipboard text={accessToken}>
                <Button color="secondary">Copy</Button>
            </CopyToClipboard>
        </div>
    )
}