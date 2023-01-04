import React, { useEffect, useState } from 'react'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { useDollyMaler } from '@/utils/hooks/useMaler'
import { useBrukerProfil } from '@/utils/hooks/useBruker'

type Props = {
	valgtMal: (mal: any) => void
}

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

export const MalValg = ({ valgtMal }: Props) => {
	const [malValue, setMalValue] = useState(null)

	const { maler, loading } = useDollyMaler()
	const { brukerProfil } = useBrukerProfil()

	const [malBruker, setMalBruker] = useState(brukerProfil?.visningsNavn)

	useEffect(() => {
		if (maler && malBruker) {
			const brukerMals = getMalOptions(maler, malBruker)
			if (brukerMals.length) {
				setMalValue(brukerMals[0].value)
				valgtMal(brukerMals[0].data)
			}
		}
	}, [maler, malBruker])

	const brukerOptions = maler ? getBrukerOptions(maler) : []
	const malOptions = maler && malBruker ? getMalOptions(maler, malBruker) : []

	return (
		<div className="flexbox--align-center--justify-start importModal-mal-velg">
			<DollySelect
				name="zIdent"
				label="Bruker"
				isLoading={loading}
				options={brukerOptions}
				size="medium"
				onChange={(e: any) => {
					setMalBruker(e.value)
				}}
				value={malBruker}
				isClearable={false}
			/>

			<DollySelect
				name="mal"
				label="Maler"
				isLoading={loading}
				options={malOptions}
				size="grow"
				onChange={(e: any) => {
					setMalValue(e.value)
					valgtMal(e.data)
				}}
				value={malValue}
				isClearable={false}
			/>
		</div>
	)
}
