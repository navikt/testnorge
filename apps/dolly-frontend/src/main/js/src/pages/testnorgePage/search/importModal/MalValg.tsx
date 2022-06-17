import React, {useEffect, useState} from 'react'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'

type Props = {
    valgtMal: (mal: any) => void,
}

const getBrukerOptions = (malbestillinger: any) =>
    Object.keys(malbestillinger).map((ident) => ({
        value: ident,
        label: ident,
    }))

const getMalOptions = (malbestillinger: any, bruker: any) => {
    if (!malbestillinger || !malbestillinger[bruker]) return []
    return malbestillinger[bruker].map((mal: any) => ({
        value: mal.id,
        label: mal.malNavn,
        data: { bestilling: mal.bestilling, malNavn: mal.malNavn },
    }))
}

export const MalValg = ({ valgtMal }: Props) => {
    const [malBruker, setMalBruker] = useState(null)
    const [malValue, setMalValue] = useState(null)
    const [maler, setMaler] = useState(null)

    useAsync(async () => {
        const response = await DollyApi.getBestillingMaler()
        setMaler(response.data)
        return response.data
    }, [])

    useAsync(async () => {
        const response = await DollyApi.getCurrentBruker()
        setMalBruker(response.data.brukernavn)
        return response.data
    }, [])

    useEffect(() => {
        if (maler && malBruker) {
            const brukerMals = getMalOptions(maler.malbestillinger, malBruker)
            if (brukerMals.length) {
                setMalValue(brukerMals[0].value)
                valgtMal(brukerMals[0].data)
            }
        }
    }, [maler, malBruker])

    const brukerOptions = maler ? getBrukerOptions(maler.malbestillinger) : []
    const malOptions = maler && malBruker ? getMalOptions(maler.malbestillinger, malBruker) : []

    return (
        <div className="flexbox--align-center--justify-start importModal-mal-velg">
            <DollySelect
                name="zIdent"
                label="Bruker"
                isLoading={maler?.loading}
                options={brukerOptions}
                size="medium"
                onChange={(e: any) => {
                    setMalBruker(e.value)
                }}
                value={malBruker}
                isClearable={false}/>

            <DollySelect
                name="mal"
                label="Maler"
                isLoading={maler?.loading}
                options={malOptions}
                size="grow"
                onChange={(e: any) => {
                    setMalValue(e.value)
                    valgtMal(e.data)
                }}
                value={malValue}
                isClearable={false}/>

        </div>
    )
}
