import React, { useState } from 'react'
import { useDollyMaler } from '@/utils/hooks/useMaler'
import { useBrukerProfil } from '@/utils/hooks/useBruker'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { Switch } from '@navikt/ds-react'
import { useBoolean } from 'react-use'

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

export const MalValg = ({ setValgtMal }) => {
	const [benyttMal, setBenyttMal] = useBoolean(false)
	const [malValue, setMalValue] = useState(null)

	const { maler, loading } = useDollyMaler()
	const { brukerProfil } = useBrukerProfil()

	const [malBruker, setMalBruker] = useState(brukerProfil?.visningsNavn)

	const brukerOptions = maler ? getBrukerOptions(maler) : []
	const malOptions = maler && malBruker ? getMalOptions(maler, malBruker) : []

	return (
		<>
			<Switch checked={benyttMal} onChange={() => setBenyttMal()}>
				Benytt mal
			</Switch>
			<div className="flexbox--align-center--justify-start">
				<DollySelect
					name="zIdent"
					label="Bruker"
					isLoading={loading}
					options={brukerOptions}
					size="large"
					onChange={(e: any) => {
						setMalBruker(e.value)
					}}
					value={malBruker}
					isDisabled={!benyttMal}
					isClearable={false}
					isInDialog={true}
				/>
				<DollySelect
					name="mal"
					label="Maler"
					isLoading={loading}
					options={malOptions}
					size="grow"
					onChange={(e: any) => {
						setValgtMal(e.data)
						setMalValue(e.value)
					}}
					isDisabled={!benyttMal}
					value={malValue}
					isInDialog={true}
				/>
			</div>
		</>
	)
}
