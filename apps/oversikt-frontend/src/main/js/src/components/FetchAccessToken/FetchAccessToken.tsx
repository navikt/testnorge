import React, {useState} from 'react';
import './FetchAccessToken.less'
// @ts-ignore
import {CopyToClipboard} from "react-copy-to-clipboard/lib/Component";
// @ts-ignore
import {fetchToken} from "../../api";
import {Fareknapp, Hovedknapp, Knapp} from "nav-frontend-knapper";

type Props = {
    name: string
}

const FetchAccessToken = ({name}: Props) => {
    const [accessToken, setAccessToken] = useState(null);
    const [loading, setLoading] = useState(false);


    const onGetToken = (name: string) => {
        setLoading(true);
        fetchToken(name).then((response: any) => {
            setAccessToken(response.token)
            setLoading(false)
        })
    }
    return (
        <div className="fetch-access-token">
            <div>
                <textarea className="access_token" disabled={true}
                          value={loading ? "Laster token..." : accessToken ? accessToken : ""}/>
            </div>
            <Hovedknapp spinner={loading} disabled={loading} onClick={() => onGetToken(name)}>Hent token</Hovedknapp>
            <CopyToClipboard text={accessToken}>
                <Fareknapp disabled={loading}>Copy</Fareknapp>
            </CopyToClipboard>
        </div>
    )
}

export default FetchAccessToken;