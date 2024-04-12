import React, { useState } from 'react'
import { useDollyMaler } from '@/utils/hooks/useMaler'
import { useBrukerProfil } from '@/utils/hooks/useBruker'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { Switch } from '@navikt/ds-react'
import { useBoolean } from 'react-use'
import styled from 'styled-components'

type MalValgProps = {
	setValgtMal: Function
}

const MalValgWrapper = styled.div`
	margin-top: 20px;
`

const getBrukerOptions = (malbestillinger: any) =>
	Object.keys(malbestillinger).map((ident) => ({
		value: ident,
		label: ident,
	}))

const getMalOptions = (malbestillinger: any, bruker: any) => {
	if (!malbestillinger || !malbestillinger[bruker]) {
		return []
	}
	return malbestillinger[bruker].map((mal: any) => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.bestilling, malNavn: mal.malNavn },
	}))
}

export const MalValg = ({ setValgtMal }: MalValgProps) => {
	const [benyttMal, setBenyttMal] = useBoolean(false)
	const [malValue, setMalValue] = useState(null)

	const { maler, loading: loadingMaler } = useDollyMaler()
	const { brukerProfil, loading: loadingBruker } = useBrukerProfil()

	const [malBruker, setMalBruker] = useState(brukerProfil?.visningsNavn)

	const brukerOptions = maler ? getBrukerOptions(maler) : []
	const malOptions = maler && malBruker ? getMalOptions(maler, malBruker) : []

	return (
		<MalValgWrapper>
			<Switch size="small" checked={benyttMal} onChange={() => setBenyttMal()}>
				Benytt mal
			</Switch>
			<div className="flexbox--align-center--justify-start" style={{ marginTop: '5px' }}>
				<DollySelect
					name="zIdent"
					label="Bruker"
					isLoading={loadingBruker}
					options={brukerOptions}
					size="medium"
					onChange={(e: any) => {
						setMalBruker(e.value)
					}}
					value={malBruker}
					isDisabled={!benyttMal}
					isClearable={false}
				/>
				<DollySelect
					name="mal"
					label="Maler"
					isLoading={loadingMaler}
					options={malOptions}
					size="grow"
					onChange={(e: any) => {
						setValgtMal(e?.data || null)
						setMalValue(e?.value || null)
					}}
					isDisabled={!benyttMal}
					value={malValue}
				/>
			</div>
		</MalValgWrapper>
	)
}
