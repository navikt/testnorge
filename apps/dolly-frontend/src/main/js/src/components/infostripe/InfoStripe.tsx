import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import { Alert } from '@navikt/ds-react'
import { Close } from "@navikt/ds-icons";
import { fetcher } from '~/api'

import './InfoStripe.less'

const getInfoStripeUrl = `/dolly-backend/api/v1/info-stripe`

type InfoStripeType = {
    text: string
    type: string
}

export const InfoStripe = () => {
    const [alert, setAlert] = useState({text: 'Arena er ned', type: 'info'})

    const { data, error } = useSWR<InfoStripeType, Error>(getInfoStripeUrl, fetcher, {
        refreshInterval: 5000,
        dedupingInterval: 5000,
    })
    console.log('data', data, error)
    if (!error && data) {
        setAlert(data)
    }

    // useEffect(() => {
    //     //setAlertText('info ' + new Date().toString())
    //     setTimeout(() => setAlert({text: 'Arena er ned', type: 'error'}), 10000)
    //     setTimeout(() => setAlert({text: 'Dolly team er på ferie, forvent lengre tid på svar', type: 'info'}), 20000)
    // }, [])

    if (!alert) {
        return null
    }
    return (
        <div className="info-stripe">
            <div className="info-stripe-content">
                <Alert variant={alert.type}>{alert.text}</Alert>
                <span><Close onClick={() => setAlert(null)} /></span>
            </div>
        </div>
    )
}
