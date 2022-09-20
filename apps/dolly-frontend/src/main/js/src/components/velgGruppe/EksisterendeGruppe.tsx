import React from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Loading from '~/components/ui/loading/Loading'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { Gruppe, useGrupper } from '~/utils/hooks/useGruppe'
import _orderBy from 'lodash/orderBy'

interface EksisterendeGruppe {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
}

type Options = {
	value: string
	label: string
}

export default ({ setValgtGruppe, valgtGruppe }: EksisterendeGruppe) => {
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()
	const { grupperById, loading } = useGrupper(brukerId)

	const sortedGruppeliste = grupperById && _orderBy(Object.values(grupperById), ['id'], ['desc'])

	const gruppeOptions = sortedGruppeliste?.map((gruppe: Gruppe) => {
		return {
			value: gruppe.id,
			label: `${gruppe.id} - ${gruppe.navn}`,
		}
	})

	if (loading) {
		return <Loading label="Laster grupper" />
	}

	return (
		<DollySelect
			name="Gruppe"
			label="Gruppe"
			options={gruppeOptions}
			onChange={(gruppe: Options) => setValgtGruppe(gruppe.value)}
			value={valgtGruppe}
			size="large"
			isClearable={false}
		/>
	)
}
