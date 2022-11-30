import React from 'react'
import { Alert } from '@navikt/ds-react'
import { Close } from '@navikt/ds-icons'

import './DollyInfoAlert.less'

type DollyInfoAlertType = {
    id: number
    text: string
    type: "error" | "warning" | "info" | "success"
    onHide: Function
}

export const DollyInfoAlert = ({id, type, text, onHide}: DollyInfoAlertType) => {
    return (
        <div className="dolly-info-alert">
            <Alert size={"small"} variant={type} style={{width: '100%'}}>{text}</Alert>
            <span><Close onClick={() => onHide(id)}/></span>
        </div>
    )
}
