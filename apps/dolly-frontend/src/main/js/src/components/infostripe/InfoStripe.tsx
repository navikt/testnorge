import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import { fetcher } from '~/api'

import './InfoStripe.less'
import {DollyInfoAlert} from "~/components/infostripe/DollyInfoAlert";

const getInfoStripeUrl = `/dolly-backend/api/v1/info-stripe`

type InfoStripeType = {
    id: number
    text: string
    type: string
    start: Date
    expires: Date
}

export const InfoStripe = () => {
    const expireDate = new Date(2023, 11, 29, 13, 26, 0)
    const arenaAlert = {id: 1, text: 'Arena er ned', type: 'error', hide: null, expires: null}
    const dollyAlert = {id: 2, text: 'Team Dolly er på ferie, forvent lengre tid på svar', type: 'info', hide: null, expires: null}

    const [alerts, setAlerts] = useState([arenaAlert])
    const [hidenAlerts, setHidenAlerts] = useState({})

    const hideAlert = (id) => {
        hidenAlerts[id] = true
        setHidenAlerts(hidenAlerts)
        setAlerts([{expires: null, text: null, type: null, id: id, hide: true}])
    }

    const { data, error } = useSWR<InfoStripeType, Error>(getInfoStripeUrl, fetcher, {
        refreshInterval: 5000,
        dedupingInterval: 5000,
    })

    if (!error && data) {
        // @ts-ignore
        setAlert(data)
    }

    useEffect(() => {
        setTimeout(() => setAlerts([arenaAlert]), 5000)
        setTimeout(() => setAlerts([arenaAlert, dollyAlert]), 10000)
        setTimeout(() => setAlerts([arenaAlert, dollyAlert]), 30000)
    }, [])

    if (!alerts) {
        return null
    }

    const visibleAlerts = alerts && alerts.filter(alert => !alert.hide)

    if (!visibleAlerts || !visibleAlerts.length) {
        return null
    }

    return (
        <div className="info-stripe">
            {visibleAlerts && visibleAlerts.map(alert => {
                return <DollyInfoAlert id={alert.id} type={alert.type} text={alert.text} onHide={hideAlert} key={alert.id} />
            })}
        </div>
    )
}
