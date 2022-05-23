import React, { useEffect } from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Loading from '~/components/ui/loading/Loading'
import { useCurrentBruker } from '~/utils/hooks/useBruker'

interface EksisterendeGruppe {
	fetchMineGrupper: (brukerId: string) => void
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	isFetching: boolean
	valgtGruppe: string
	gruppeListe: Array<Gruppe>
	mineIds: Array<string>
}

type Gruppe = {
	id: string
	navn: string
}

type Options = {
	value: string
	label: string
}

export default ({
	fetchMineGrupper,
	setValgtGruppe,
	isFetching,
	valgtGruppe,
	gruppeListe,
}: EksisterendeGruppe) => {
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()
	useEffect(() => {
		fetchMineGrupper(brukerId)
	}, [brukerId])

	const gruppeliste = gruppeListe.map((v: Gruppe) => ({
		value: v.id,
		label: v.id + ' - ' + v.navn,
	}))

	if (isFetching) return <Loading label="Laster grupper" />

	return (
		<DollySelect
			name="Gruppe"
			label="Gruppe"
			options={gruppeliste}
			onChange={(gruppe: Options) => setValgtGruppe(gruppe.value)}
			value={valgtGruppe}
			size="large"
			isClearable={false}
		/>
	)
}
