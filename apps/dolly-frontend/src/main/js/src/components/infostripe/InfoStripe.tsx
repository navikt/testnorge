import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import { fetcher } from '~/api'

import './InfoStripe.less'
import { DollyInfoAlert } from '~/components/infostripe/DollyInfoAlert'
import Button from "~/components/ui/button/Button";

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
    const arenaAlert = {id: 1, text: 'Arena er nede', type: 'error', hide: null, expires: null}
    const dollyAlert = {id: 2, text: 'Team Dolly er på ferie, forvent lengre tid på svar', type: 'info', hide: null, expires: null}

    const [alerts, setAlerts] = useState([arenaAlert])
    const [showAll, setShowAll] = useState(false)
    const [visibleAlerts, setVisibleAlerts] = useState([])
    const [hiddenAlerts, setHiddenAlerts] = useState([])

    const hideAlert = (id) => {
        hiddenAlerts.push(id)
        setHiddenAlerts(hiddenAlerts)

        const alert = alerts.find(alert => alert.id === id)
        if (alert) {
            alert.hide = true
            setAlerts(alerts)

            const newVisibleAlerts = alerts && alerts.filter(alert => {
                console.log('is alert hiden for ', alert.id)
                return !alert.hide
            })
            setVisibleAlerts(newVisibleAlerts)
        }
    }

    const invertVisAlleMeldinger = () => {
        setShowAll(!showAll)
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
        setTimeout(() => {setAlerts([arenaAlert]); setVisibleAlerts([arenaAlert]) }, 1000)
        setTimeout(() => {setAlerts([arenaAlert, dollyAlert]); setVisibleAlerts([arenaAlert, dollyAlert]) }, 10000)
        setTimeout(() => {setAlerts([arenaAlert, dollyAlert]); setVisibleAlerts([arenaAlert, dollyAlert]) }, 30000)
    }, [])

    if (!alerts) {
        return null
    }

    console.log('visible alert', visibleAlerts, hiddenAlerts)
    if (!visibleAlerts || !visibleAlerts.length) {
        return null
    }

    const expandButtonText = !showAll ? `Vis alle meldinger ( ${visibleAlerts.length})`: 'Vis bare én meldinge'

    return (
        <div className="info-stripe">
            {!showAll &&
                <DollyInfoAlert id={visibleAlerts[0].id} type={visibleAlerts[0].type} text={visibleAlerts[0].text} onHide={hideAlert} key={visibleAlerts[0].id} />
            }
            {showAll && visibleAlerts.map(alert => {
                return <DollyInfoAlert id={alert.id} type={alert.type} text={alert.text} onHide={hideAlert} key={alert.id} />
            })}
            {visibleAlerts.length > 1 &&
                <div>
                    <Button onClick={invertVisAlleMeldinger} kind={showAll === false ? 'expand' : 'collapse'}>
                        {expandButtonText}
                    </Button>
                </div>
            }
        </div>
    )
}
